package backend.utils;

import backend.enums.Register;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.instruction.ZextInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RegAlloc {
    private static final ArrayList<Register> registerPool = new ArrayList<>();
    private static final int length = 18;
    private static HashMap<BasicBlock, HashSet<Value>> inMap;
    private static HashMap<BasicBlock, HashSet<Value>> outMap;
    // 先赋值后使用
    private static HashMap<BasicBlock, HashSet<Value>> defMap;
    // 先使用后赋值
    private static HashMap<BasicBlock, HashSet<Value>> useMap;
    private static HashMap<Register, Value> reg2var;
    private static HashMap<Value, Register> var2reg;
    private static int pointer = 0;

    public static void run(Module module) {
        for (Register reg : Register.values()) {
            if (reg.ordinal() >= Register.T0.ordinal()
                    && reg.ordinal() <= Register.T9.ordinal()) {
                registerPool.add(reg);
            }
        }
        // 不需要翻译zext指令，将char和int都当32位处理
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions
                        = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof ZextInst zextInst) {
                        Value value = zextInst.getOriginValue();
                        zextInst.replaceByNewValue(value);
                        block.getInstructions().remove(zextInst);
                        zextInst.deleteUse();
                    }
                }
            }
        }
        for (Function function : module.getFunctions()) {
            init(function);
            calcInOut(function);
            alloc(function.getBasicBlocks().get(0));
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (instruction instanceof CallInst callInst) {
                        HashSet<Register> regs = new HashSet<>();
                        for (Value value : outMap.get(block)) {
                            if (var2reg.containsKey(value)) {
                                regs.add(var2reg.get(value));
                            }
                        }
                        for (int i = block.getInstructions().indexOf(callInst) + 1;
                             i < block.getInstructions().size(); i++) {
                            for (Value value : block.getInstructions().get(i).getOperands()) {
                                if (var2reg.containsKey(value)) {
                                    regs.add(var2reg.get(value));
                                }
                            }
                        }
                        callInst.setActiveReg(regs);
                    }
                }
            }
            for (Value value : var2reg.keySet()) {
                System.out.println(value.getName() + "->" + var2reg.get(value));
            }
            function.setVar2reg(var2reg);
        }
    }

    private static void init(Function function) {
        inMap = new HashMap<>();
        outMap = new HashMap<>();
        defMap = new HashMap<>();
        useMap = new HashMap<>();
        var2reg = new HashMap<>();
        reg2var = new HashMap<>();
        function.getBasicBlocks().forEach(basicBlock -> {
            inMap.put(basicBlock, new HashSet<>());
            outMap.put(basicBlock, new HashSet<>());
            defMap.put(basicBlock, new HashSet<>());
            useMap.put(basicBlock, new HashSet<>());
            calcDefUse(basicBlock);
        });
    }

    private static void calcDefUse(BasicBlock basicBlock) {
        HashSet<Value> defSet = defMap.get(basicBlock);
        HashSet<Value> useSet = useMap.get(basicBlock);
        for (Instruction instruction : basicBlock.getInstructions()) {
            if (instruction instanceof PhiInst phiInst) {
                for (Value operand : phiInst.getOperands()) {
                    if (!(operand instanceof ConstInt)) {
                        useSet.add(operand);
                    }
                }
            }
        }
        for (Instruction instruction : basicBlock.getInstructions()) {
            for (Value operand : instruction.getOperands()) {
                if (!(operand instanceof ConstInt)) {
                    useSet.add(operand);
                }
            }
            if (!useSet.contains(instruction) && !instruction.getName().isEmpty()) {
                defSet.add(instruction);
            }
        }
    }

    private static void calcInOut(Function function) {
        ArrayList<BasicBlock> blocks = function.getBasicBlocks();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock block = blocks.get(i);
                HashSet<Value> outSet = new HashSet<>();
                for (BasicBlock next : block.getNextBlocks()) {
                    outSet.addAll(inMap.get(next));
                }
                outMap.put(block, outSet);
                HashSet<Value> inSet = new HashSet<>(outSet);
                inSet.removeAll(defMap.get(block));
                inSet.addAll(useMap.get(block));
                if (!inSet.equals(inMap.get(block))) {
                    changed = true;
                    inMap.put(block, inSet);
                }
            }
        }
    }

    private static void alloc(BasicBlock block) {
        ArrayList<Instruction> instructions = block.getInstructions();
        HashSet<Value> defined = new HashSet<>();
        HashSet<Value> neverUsedAfter = new HashSet<>();
        // value的最后一次使用的位置在key
        HashMap<Value, Value> lastUse = new HashMap<>();

        for (Instruction instruction : instructions) {
            for (Value operand : instruction.getOperands()) {
                lastUse.put(operand, instruction);
            }
        }
        for (Instruction instruction : instructions) {
            if (!(instruction instanceof PhiInst)) {
                ArrayList<Value> operands = instruction.getOperands();
                for (Value operand : operands) {
                    if (lastUse.get(operand).equals(instruction)
                            && !outMap.get(block).contains(operand)
                            && var2reg.containsKey(operand)) {
                        reg2var.remove(var2reg.get(operand));
                        neverUsedAfter.add(operand);
                    }
                }
            }
            if (!instruction.getName().isEmpty() && !(instruction instanceof ZextInst)) {
                defined.add(instruction);
                Register reg = getReg();
                if (reg2var.containsKey(reg)) {
                    var2reg.remove(reg2var.get(reg));
                }
                reg2var.put(reg, instruction);
                var2reg.put(instruction, reg);
            }
        }
        for (BasicBlock imm : block.getImmediateDominateBlocks()) {
            // 记录映射关系，为直接支配的子块分配寄存器
            HashMap<Register, Value> buffer = new HashMap<>();
            for (Register reg : reg2var.keySet()) {
                Value value = reg2var.get(reg);
                if (!inMap.get(imm).contains(value)) {
                    buffer.put(reg, value);
                }
            }
            for (Register reg : buffer.keySet()) {
                reg2var.remove(reg);
            }
            alloc(imm);
            for (Register reg : buffer.keySet()) {
                reg2var.put(reg, buffer.get(reg));
            }
        }
        for (Value value : defined) {
            if (var2reg.containsKey(value)) {
                reg2var.remove(var2reg.get(value));
            }
        }
        for (Value value : neverUsedAfter) {
            if (var2reg.containsKey(value) && !defined.contains(value)) {
                reg2var.put(var2reg.get(value), value);
            }
        }
    }

    private static Register getReg() {
        for (Register reg : registerPool) {
            if (!reg2var.containsKey(reg)) {
                return reg;
            }
        }
        pointer = (pointer + 1) % length;
        return registerPool.get(pointer);
    }

}
