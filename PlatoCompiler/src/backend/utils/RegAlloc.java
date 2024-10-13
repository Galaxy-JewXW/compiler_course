package backend.utils;

import backend.enums.Register;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.ConstString;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.instruction.ZextInst;
import middle.component.model.Value;
import optimize.Mem2Reg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 寄存器分配器，用于将变量映射到物理寄存器。
 */
public class RegAlloc {
    // 活跃变量分析映射
    private static HashMap<BasicBlock, HashSet<Value>> inMap;    // 每个基本块的入口活跃变量集合
    private static HashMap<BasicBlock, HashSet<Value>> outMap;   // 每个基本块的出口活跃变量集合
    private static HashMap<BasicBlock, HashSet<Value>> defMap;   // 定义集合：先赋值后使用的变量
    private static HashMap<BasicBlock, HashSet<Value>> useMap;   // 使用集合：先使用后赋值的变量

    // 寄存器分配
    private static ArrayList<Register> registerPool;             // 可用寄存器池
    private static int registerCount;                            // 寄存器数量
    private static HashMap<Register, Value> regToVarMap;         // 寄存器到变量的映射
    private static HashMap<Value, Register> varToRegMap;         // 变量到寄存器的映射
    private static int registerIndex = 0;                        // 轮询寄存器索引
    private static HashSet<BasicBlock> visitedBlocks;            // 已访问的基本块集合，防止重复访问

    public static void run(Module module) {
        Mem2Reg.run(module, false);

        registerPool = new ArrayList<>();
        for (Register register : Register.values()) {
            if (register.ordinal() >= Register.T0.ordinal() && register.ordinal() <= Register.T9.ordinal()) {
                registerPool.add(register);
            }
        }
        registerCount = registerPool.size();

        for (Function function : module.getFunctions()) {
            initLiveVariableAnalysis(function);
            computeInOutSets(function);
            regToVarMap = new HashMap<>();
            varToRegMap = new HashMap<>();
            visitedBlocks = new HashSet<>();
            allocateRegisters(function.getEntryBlock());
            function.setVar2reg(varToRegMap);
        }
    }

    /**
     * 初始化给定函数的活跃变量分析数据结构。
     */
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

    /**
     * 计算给定基本块的定义和使用集合。
     * 定义集合包含在块中被赋值的变量（先赋值后使用）。
     * 使用集合包含在块中被使用的变量（先使用后赋值）。
     */
    private static void computeDefUseSets(BasicBlock block) {
        HashSet<Value> defSet = defMap.get(block);
        HashSet<Value> useSet = useMap.get(block);

        // 特别处理 Phi 指令
        for (Instruction instruction : block.getInstructions()) {
            if (instruction instanceof PhiInst) {
                for (Value value : instruction.getOperands()) {
                    if (!(value instanceof ConstInt || value instanceof ConstString)) {
                        useSet.add(value);
                    }
                }
            }
        }

        // 处理其他指令
        for (Instruction instruction : block.getInstructions()) {
            for (Value operand : instruction.getOperands()) {
                if (!(operand instanceof ConstInt || operand instanceof ConstString) && !defSet.contains(operand)) {
                    useSet.add(operand);
                }
            }
            if (!useSet.contains(instruction) && !instruction.getName().isEmpty()) {
                defSet.add(instruction);
            }
        }
    }

    /**
     * 计算函数中每个基本块的活跃入口和出口集合。
     */
    private static void computeInOutSets(Function function) {
        ArrayList<BasicBlock> blocks = function.getBasicBlocks();
        boolean changed = true;

        // 迭代进行数据流分析，直到集合不再发生变化
        while (changed) {
            changed = false;
            // 逆序遍历基本块，提高效率
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock block = blocks.get(i);
                HashSet<Value> outSet = new HashSet<>();
                // 出口集合是所有后继块的入口集合的并集
                for (BasicBlock successor : block.getNextBlocks()) {
                    outSet.addAll(inMap.get(successor));
                }
                outMap.put(block, outSet);

                HashSet<Value> inSet = new HashSet<>(outSet);
                // 入口集合 = （出口集合 - 定义集合）∪ 使用集合
                inSet.removeAll(defMap.get(block));
                inSet.addAll(useMap.get(block));

                if (!inSet.equals(inMap.get(block))) {
                    changed = true;
                    inMap.put(block, inSet);
                }
            }
        }
    }

    /**
     * 为给定的基本块分配寄存器。
     */
    private static void allocateRegisters(BasicBlock block) {
        // 防止无限递归，检查是否已访问过
        if (visitedBlocks.contains(block)) {
            return;
        }
        visitedBlocks.add(block); // 标记基本块已访问

        HashMap<Value, Instruction> lastUseMap = new HashMap<>();
        HashSet<Value> unusedAfterBlock = new HashSet<>();
        HashSet<Value> definedInBlock = new HashSet<>();

        // 找到基本块中每个变量的最后一次使用
        for (Instruction instruction : block.getInstructions()) {
            for (Value operand : instruction.getOperands()) {
                lastUseMap.put(operand, instruction);
            }
        }

        for (Instruction instruction : block.getInstructions()) {
            if (!(instruction instanceof PhiInst)) {
                for (Value operand : instruction.getOperands()) {
                    if (lastUseMap.get(operand) == instruction && !outMap.get(block).contains(operand)) {
                        // 如果这是操作数的最后一次使用，且在块外不再活跃，释放其寄存器
                        if (varToRegMap.containsKey(operand)) {
                            regToVarMap.remove(varToRegMap.get(operand));
                            unusedAfterBlock.add(operand);
                        }
                    }
                }
            }
            if (!instruction.getName().isEmpty() && !(instruction instanceof ZextInst)) {
                // 为指令结果分配寄存器
                definedInBlock.add(instruction);
                Register register = getRegister();
                // 如果寄存器已被占用，移除旧的映射
                if (regToVarMap.containsKey(register)) {
                    varToRegMap.remove(regToVarMap.get(register));
                }
                regToVarMap.put(register, instruction);
                varToRegMap.put(instruction, register);
            }
        }

        // 处理子基本块（直接支配的基本块）
        for (BasicBlock childBlock : block.getImmediateDominateBlocks()) {
            // 保存当前寄存器映射中不在子块入口活跃集合中的变量
            HashMap<Register, Value> savedMappings = new HashMap<>();
            for (Register register : regToVarMap.keySet()) {
                if (!inMap.get(childBlock).contains(regToVarMap.get(register))) {
                    savedMappings.put(register, regToVarMap.get(register));
                }
            }
            // 从当前映射中移除保存的映射
            for (Register register : savedMappings.keySet()) {
                regToVarMap.remove(register);
            }
            // 递归地为子块分配寄存器
            allocateRegisters(childBlock);
            // 恢复保存的映射
            for (Register register : savedMappings.keySet()) {
                regToVarMap.put(register, savedMappings.get(register));
            }
        }

        // 处理完后，从寄存器映射中移除在此块中定义的变量
        for (Value value : definedInBlock) {
            if (varToRegMap.containsKey(value)) {
                regToVarMap.remove(varToRegMap.get(value));
            }
        }
        // 恢复在此块后不再使用的变量的映射
        for (Value value : unusedAfterBlock) {
            if (varToRegMap.containsKey(value) && !definedInBlock.contains(value)) {
                regToVarMap.put(varToRegMap.get(value), value);
            }
        }
    }

    /**
     * 从寄存器池中获取一个可用的寄存器。
     * 如果没有可用的寄存器，以轮询方式使用寄存器。
     */
    private static Register getRegister() {
        // 尝试找到一个空闲的寄存器
        for (Register register : registerPool) {
            if (!regToVarMap.containsKey(register)) {
                return register;
            }
        }
        // 如果所有寄存器都已被占用，轮询使用寄存器
        if (registerIndex >= registerCount) {
            registerIndex = 0;
        }
        return registerPool.get(registerIndex++);
    }
}
