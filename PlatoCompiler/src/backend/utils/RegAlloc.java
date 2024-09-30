package backend.utils;

import backend.enums.Register;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.AllocInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;
import middle.component.type.ArrayType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegAlloc {
    private static HashMap<Value, Register> var2reg;
    private static HashMap<Register, Value> reg2var;
    private static HashMap<Value, Integer> useCnt;
    private static Function currentFunction;
    private static ArrayList<Register> regSet;

    public static void run(Module module) {
        regSet = new ArrayList<>(Arrays.asList(Register.values())
                .subList(Register.T0.ordinal(), Register.T9.ordinal() + 1));
        for (Function func : module.getFunctions()) {
            init(func);
            visitBlock(func.getEntryBlock());
            updateCallInst(func);
            var2reg.forEach((key, value) -> System.out.println(key.getName() + "->" + value));
            func.setVar2reg(var2reg);
        }
    }

    private static void init(Function func) {
        var2reg = new HashMap<>();
        reg2var = new HashMap<>();
        useCnt = new HashMap<>();
        currentFunction = func;
        initUseCnt();
    }

    private static void initUseCnt() {
        for (BasicBlock block : currentFunction.getBasicBlocks()) {
            for (Instruction instruction : block.getInstructions()) {
                updateUseCnt(instruction);
            }
        }
    }

    private static void updateUseCnt(Instruction instruction) {
        for (Value value : instruction.getOperands()) {
            useCnt.merge(value, 1, Integer::sum);
        }
        if (!instruction.getName().isEmpty()) {
            useCnt.merge(instruction, 1, Integer::sum);
        }
    }

    private static void visitBlock(BasicBlock entryBlock) {
        ArrayList<Instruction> instructions = new ArrayList<>(entryBlock.getInstructions());
        HashSet<Value> localDefs = new HashSet<>();
        HashMap<Value, Instruction> lastUse = new HashMap<>();
        HashSet<Value> neverUsed = new HashSet<>();

        for (Instruction instruction : instructions) {
            for (Value value : instruction.getOperands()) {
                lastUse.put(value, instruction);
            }
        }
        buildInstructions(entryBlock, instructions, localDefs, lastUse, neverUsed);
        buildChildBlock(entryBlock);
        cleanAfterVisit(localDefs, neverUsed);
    }

    private static void buildInstructions(BasicBlock entry, List<Instruction> instrs, Set<Value> localDefed,
                                          Map<Value, Instruction> lastUse, Set<Value> neverUsed) {
        for (Instruction instruction : instrs) {
            if (!(instruction instanceof PhiInst)) {
                freeUnusedRegs(instruction, entry, lastUse, neverUsed);
            }
            if (!instruction.getName().isEmpty() &&
                    !(instruction instanceof AllocInst
                            && instruction.getValueType() instanceof ArrayType)) {
                localDefed.add(instruction);
                tryAllocReg(instruction);
            }
        }
    }

    private static void freeUnusedRegs(Instruction instruction, BasicBlock entry,
                                       Map<Value, Instruction> lastUse, Set<Value> neverUsed) {
        for (Value operand : instruction.getOperands()) {
            if (lastUse.get(operand) == instruction && !entry.getOutSet().contains(operand) && var2reg.containsKey(operand)) {
                reg2var.remove(var2reg.get(operand));
                neverUsed.add(operand);
            }
        }
    }

    private static void buildChildBlock(BasicBlock entryBlock) {
        for (BasicBlock block : entryBlock.getImmediateDominateBlocks()) {
            Map<Register, Value> curChildNeverUse = new HashMap<>();
            for (Map.Entry<Register, Value> entry : reg2var.entrySet()) {
                if (!block.getInSet().contains(entry.getValue())) {
                    curChildNeverUse.put(entry.getKey(), entry.getValue());
                }
            }
            reg2var.keySet().removeAll(curChildNeverUse.keySet());
            visitBlock(block);
            reg2var.putAll(curChildNeverUse);
        }
    }

    private static void cleanAfterVisit(Set<Value> localDefs, Set<Value> neverUsed) {
        for (Value value : localDefs) {
            if (var2reg.containsKey(value)) {
                reg2var.remove(var2reg.get(value));
            }
        }
        for (Value value : neverUsed) {
            if (!localDefs.contains(value) && var2reg.containsKey(value)) {
                reg2var.put(var2reg.get(value), value);
            }
        }
    }

    private static void tryAllocReg(Value value) {
        Register allocReg = findFreeRegister();
        if (allocReg == null) {
            allocReg = findLeastUsedRegister(value);
            if (allocReg == null) return;
        }
        if (reg2var.containsKey(allocReg)) {
            var2reg.remove(reg2var.get(allocReg));
        }
        var2reg.put(value, allocReg);
        reg2var.put(allocReg, value);
    }

    private static Register findFreeRegister() {
        return regSet.stream()
                .filter(reg -> !reg2var.containsKey(reg))
                .findFirst()
                .orElse(null);
    }

    private static Register findLeastUsedRegister(Value value) {
        return regSet.stream()
                .min(Comparator.comparingInt(reg -> useCnt.getOrDefault
                        (reg2var.get(reg), Integer.MAX_VALUE)))
                .filter(reg -> useCnt.getOrDefault(value, 0)
                        >= useCnt.getOrDefault(reg2var.get(reg), Integer.MAX_VALUE))
                .orElse(null);
    }

    private static void updateCallInst(Function func) {
        for (BasicBlock block : func.getBasicBlocks()) {
            for (Instruction instruction : block.getInstructions()) {
                if (instruction instanceof CallInst call) {
                    HashSet<Register> activeRegs = new HashSet<>();
                    collectActiveRegisters(block, call, activeRegs);
                    call.setActiveReg(activeRegs);
                }
            }
        }
    }

    private static void collectActiveRegisters(BasicBlock block, CallInst call,
                                               HashSet<Register> activeRegs) {
        block.getOutSet().stream()
                .filter(var2reg::containsKey)
                .forEach(value -> activeRegs.add(var2reg.get(value)));

        int callIndex = block.getInstructions().indexOf(call);
        block.getInstructions().subList(callIndex + 1,
                        block.getInstructions().size()).stream()
                .flatMap(instr -> instr.getOperands().stream())
                .filter(var2reg::containsKey)
                .forEach(value -> activeRegs.add(var2reg.get(value)));
    }
}