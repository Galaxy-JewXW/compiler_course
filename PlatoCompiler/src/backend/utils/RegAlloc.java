package backend.utils;

import backend.enums.Register;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.ConstString;
import middle.component.FuncParam;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.instruction.ZextInst;
import middle.component.model.Value;
import optimize.Mem2Reg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * 寄存器分配器，使用图着色算法将变量映射到物理寄存器。
 */
public class RegAlloc {
    // 活跃变量分析映射
    private static HashMap<BasicBlock, HashSet<Value>> inMap;    // 每个基本块的入口活跃变量集合
    private static HashMap<BasicBlock, HashSet<Value>> outMap;   // 每个基本块的出口活跃变量集合
    private static HashMap<BasicBlock, HashSet<Value>> defMap;   // 定义集合：在块中被定义的变量
    private static HashMap<BasicBlock, HashSet<Value>> useMap;   // 使用集合：在块中先使用后定义的变量

    private static int registerCount;                            // 可用寄存器数量
    private static HashMap<Value, InterferenceGraphNode> valueNodeMap; // 变量到干涉图节点的映射

    private static boolean aggressive = false;

    // 干涉图节点集合
    private static HashSet<InterferenceGraphNode> graphNodes;

    public static void run(Module module) {
        Mem2Reg.run(module, false);

        ArrayList<Register> registerPool = new ArrayList<>();
        for (Register register : Register.values()) {
            if (register.ordinal() >= Register.T0.ordinal() && register.ordinal() <= Register.T9.ordinal()) {
                registerPool.add(register);
            }
        }
        if (aggressive) {
            registerPool.add(Register.GP);
            registerPool.add(Register.FP);
        }
        registerCount = registerPool.size();

        for (Function function : module.getFunctions()) {
            initLiveVariableAnalysis(function);
            computeInOutSets(function);
            buildInterferenceGraph(function);
            colorGraph();

            HashMap<Value, Register> varToRegMap = new HashMap<>();
            for (InterferenceGraphNode node : valueNodeMap.values()) {
                if (node.isSpilled) {
                    continue;
                }
                varToRegMap.put(node.value, registerPool.get(node.color));
                System.out.println(node.value.getName() + " -> " + registerPool.get(node.color));
            }
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (!(instruction instanceof CallInst callInst)) {
                        continue;
                    }
                    HashSet<Register> regSet = new HashSet<>();
                    for (Value value : outMap.get(block)) {
                        if (varToRegMap.containsKey(value)) {
                            regSet.add(varToRegMap.get(value));
                        }
                    }
                    for (int i = block.getInstructions().indexOf(callInst) + 1;
                         i < block.getInstructions().size(); i++) {
                        for (Value value : block.getInstructions().get(i).getOperands()) {
                            if (varToRegMap.containsKey(value)) {
                                regSet.add(varToRegMap.get(value));
                            }
                        }
                    }
                    callInst.setActiveReg(regSet);
                }
            }
            function.setVar2reg(varToRegMap);
        }
    }

    private static void initLiveVariableAnalysis(Function function) {
        inMap = new HashMap<>();
        outMap = new HashMap<>();
        defMap = new HashMap<>();
        useMap = new HashMap<>();

        for (BasicBlock block : function.getBasicBlocks()) {
            inMap.put(block, new HashSet<>());
            outMap.put(block, new HashSet<>());
            defMap.put(block, new HashSet<>());
            useMap.put(block, new HashSet<>());
            computeDefUseSets(block);
        }
    }

    private static void computeDefUseSets(BasicBlock block) {
        HashSet<Value> defSet = defMap.get(block);
        HashSet<Value> useSet = useMap.get(block);

        for (Instruction instruction : block.getInstructions()) {
            if (instruction instanceof PhiInst) {
                for (Value value : instruction.getOperands()) {
                    if (isAllocatableValue(value) && !defSet.contains(value)) {
                        useSet.add(value);
                    }
                }
            }
        }

        for (Instruction instruction : block.getInstructions()) {
            for (Value operand : instruction.getOperands()) {
                if (isAllocatableValue(operand) && !defSet.contains(operand)) {
                    useSet.add(operand);
                }
            }
            if (!instruction.getName().isEmpty() && !(instruction instanceof ZextInst)) {
                defSet.add(instruction);
            }
        }
    }

    private static void computeInOutSets(Function function) {
        ArrayList<BasicBlock> blocks = function.getBasicBlocks();
        boolean changed = true;

        // 迭代进行数据流分析，直到IN和OUT集合不再发生变化
        while (changed) {
            changed = false;
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock block = blocks.get(i);
                HashSet<Value> outSet = new HashSet<>();
                // OUT[B] = 所有后继块的IN集合的并集
                for (BasicBlock successor : block.getNextBlocks()) {
                    outSet.addAll(inMap.get(successor));
                }

                for (BasicBlock successor : block.getNextBlocks()) {
                    for (Instruction instruction : successor.getInstructions()) {
                        if (instruction instanceof PhiInst phi) {
                            int index = phi.getBlocks().indexOf(block);
                            if (index >= 0) {
                                Value value = phi.getOperands().get(index);
                                if (isAllocatableValue(value)) {
                                    outSet.add(value);
                                }
                            }
                        } else {
                            break; // 非Phi指令，跳出循环
                        }
                    }
                }

                outMap.put(block, outSet);

                HashSet<Value> inSet = new HashSet<>(outSet);
                // IN[B] = USE[B] ∪ (OUT[B] - DEF[B])
                inSet.removeAll(defMap.get(block));
                inSet.addAll(useMap.get(block));

                if (!inSet.equals(inMap.get(block))) {
                    changed = true;
                    inMap.put(block, inSet);
                }
            }
        }
    }

    private static void buildInterferenceGraph(Function function) {
        graphNodes = new HashSet<>();
        valueNodeMap = new HashMap<>();

        for (BasicBlock block : function.getBasicBlocks()) {
            HashSet<Value> live = new HashSet<>(outMap.get(block));
            List<Instruction> instructions = block.getInstructions();
            ListIterator<Instruction> iterator = instructions.listIterator(instructions.size());

            while (iterator.hasPrevious()) {
                Instruction instruction = iterator.previous();
                if (!instruction.getName().isEmpty() && !(instruction instanceof ZextInst)) {
                    live.remove(instruction);
                    InterferenceGraphNode nodeU = getOrCreateNode(instruction);
                    for (Value v : live) {
                        InterferenceGraphNode nodeV = getOrCreateNode(v);
                        addEdge(nodeU, nodeV);
                    }
                }
                for (Value operand : instruction.getOperands()) {
                    if (isAllocatableValue(operand)) {
                        live.add(operand);
                    }
                }
            }
        }
    }

    private static InterferenceGraphNode getOrCreateNode(Value value) {
        if (valueNodeMap.containsKey(value)) {
            return valueNodeMap.get(value);
        } else {
            InterferenceGraphNode node = new InterferenceGraphNode(value);
            valueNodeMap.put(value, node);
            graphNodes.add(node);
            return node;
        }
    }

    private static void addEdge(InterferenceGraphNode u, InterferenceGraphNode v) {
        if (u == v) return;
        if (!u.neighbors.contains(v)) {
            u.neighbors.add(v);
            u.degree++;
        }
        if (!v.neighbors.contains(u)) {
            v.neighbors.add(u);
            v.degree++;
        }
    }

    private static void colorGraph() {
        Stack<InterferenceGraphNode> selectStack = new Stack<>();
        HashSet<InterferenceGraphNode> workList = new HashSet<>(graphNodes);

        while (!workList.isEmpty()) {
            boolean found = false;
            Iterator<InterferenceGraphNode> iterator = workList.iterator();
            while (iterator.hasNext()) {
                InterferenceGraphNode node = iterator.next();
                if (node.degree < registerCount) {
                    iterator.remove();
                    for (InterferenceGraphNode neighbor : node.neighbors) {
                        neighbor.degree--;
                    }
                    selectStack.push(node);
                    found = true;
                }
            }
            if (!found) {
                // Spill阶段，选择度数最大的节点进行溢出
                InterferenceGraphNode spillNode = selectSpillNode(workList);
                workList.remove(spillNode);
                for (InterferenceGraphNode neighbor : spillNode.neighbors) {
                    neighbor.degree--;
                }
                selectStack.push(spillNode);
            }
        }

        // Select阶段
        while (!selectStack.isEmpty()) {
            InterferenceGraphNode node = selectStack.pop();
            HashSet<Integer> neighborColors = new HashSet<>();
            for (InterferenceGraphNode neighbor : node.neighbors) {
                if (neighbor.color != -1) {
                    neighborColors.add(neighbor.color);
                }
            }
            // 寻找可用的颜色（寄存器）
            int color = -1;
            for (int i = 0; i < registerCount; i++) {
                if (!neighborColors.contains(i)) {
                    color = i;
                    break;
                }
            }
            if (color != -1) {
                node.color = color;
            } else {
                node.isSpilled = true;
            }
        }
    }

    private static InterferenceGraphNode selectSpillNode(HashSet<InterferenceGraphNode> nodes) {
        InterferenceGraphNode spillNode = null;
        int maxDegree = -1;
        for (InterferenceGraphNode node : nodes) {
            if (node.degree > maxDegree) {
                maxDegree = node.degree;
                spillNode = node;
            }
        }
        return spillNode;
    }

    private static boolean isAllocatableValue(Value value) {
        return !(value instanceof ConstInt || value instanceof ConstString
                || (value instanceof GlobalVar)
                || value instanceof BasicBlock || value instanceof Function
                || value instanceof FuncParam);
    }

    private static class InterferenceGraphNode {
        Value value;                                   // 对应的变量
        HashSet<InterferenceGraphNode> neighbors;      // 相邻节点集合
        boolean isSpilled;                             // 是否需要溢出
        int degree;                                    // 度数（相邻节点数量）
        int color;                                     // 分配的颜色（寄存器编号）

        public InterferenceGraphNode(Value value) {
            this.value = value;
            this.neighbors = new HashSet<>();
            this.isSpilled = false;
            this.degree = 0;
            this.color = -1; // -1表示未着色
        }
    }
}
