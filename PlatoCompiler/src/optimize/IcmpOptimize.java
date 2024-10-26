package optimize;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BinaryInst;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;
import java.util.HashSet;

public class IcmpOptimize {
    public static void run(Module module) {
        Mem2Reg.run(module, false);
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions
                        = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof BinaryInst binaryInst
                            && OperatorType.isLogicalOperator(binaryInst.getOpType())) {
                        icmpOptimize(binaryInst);
                    }
                }
            }
        }
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions
                        = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof BrInst brInst
                            && brInst.isConditional()) {
                        brOptimize(brInst);
                    }
                }
            }
        }
        SurplusBlock.build(module);
        CodeRemoval.run(module);
        Mem2Reg.run(module, false);
        for (Function function : module.getFunctions()) {
            HashSet<BasicBlock> reached = findReachableBlocks(function);
            function.getBasicBlocks().removeIf(bb -> {
                if (reached.contains(bb)) {
                    return false;
                }
                bb.setDeleted(true);
                return true;
            });
        }
        PhiOptimize.run(module);
    }

    private static void icmpOptimize(BinaryInst binaryInst) {
        Value value1 = binaryInst.getOperand1();
        Value value2 = binaryInst.getOperand2();
        if (value1 instanceof ConstInt constInt1 && value2 instanceof ConstInt constInt2) {
            OperatorType op = binaryInst.getOpType();
            int cons1 = constInt1.getIntValue();
            int cons2 = constInt2.getIntValue();
            int value = switch (op) {
                case ICMP_EQ -> cons1 == cons2 ? 1 : 0;
                case ICMP_NE -> cons1 != cons2 ? 1 : 0;
                case ICMP_SGE -> cons1 >= cons2 ? 1 : 0;
                case ICMP_SGT -> cons1 > cons2 ? 1 : 0;
                case ICMP_SLE -> cons1 <= cons2 ? 1 : 0;
                case ICMP_SLT -> cons1 < cons2 ? 1 : 0;
                default -> 0;
            };
            ConstInt constInt = new ConstInt(IntegerType.i1, value);
            BasicBlock curBlock = binaryInst.getBasicBlock();
            curBlock.getInstructions().remove(binaryInst);
            binaryInst.replaceByNewValue(constInt);
            binaryInst.removeOperands();
        }
    }

    private static void brOptimize(BrInst brInst) {
        Value value = brInst.getCondition();
        BasicBlock curBlock = brInst.getBasicBlock();
        if (value instanceof ConstInt constInt) {
            BrInst noCondBr;
            BasicBlock targetBlock;
            if (constInt.getIntValue() == 0) {
                targetBlock = brInst.getFalseBlock();
            } else {
                targetBlock = brInst.getTrueBlock();
            }
            noCondBr = new BrInst(targetBlock);
            curBlock.getInstructions().set(curBlock.getInstructions().indexOf(brInst), noCondBr);
            brInst.removeOperands();
        }
    }

    private static HashSet<BasicBlock> findReachableBlocks(Function function) {
        HashSet<BasicBlock> reachable = new HashSet<>();
        ArrayList<BasicBlock> workList = new ArrayList<>();
        if (!function.getBasicBlocks().isEmpty()) {
            BasicBlock entry = function.getEntryBlock();
            workList.add(entry);
            reachable.add(entry);
        }
        while (!workList.isEmpty()) {
            BasicBlock current = workList.remove(workList.size() - 1);
            for (BasicBlock suc : current.getNextBlocks()) {
                if (!reachable.contains(suc)) {
                    reachable.add(suc);
                    workList.add(suc);
                }
            }
        }
        return reachable;
    }

}
