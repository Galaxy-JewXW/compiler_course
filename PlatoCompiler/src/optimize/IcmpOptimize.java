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
            HashSet<BasicBlock> buffer = new HashSet<>();
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions
                        = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof BrInst brInst
                            && brInst.isConditional()) {
                        brOptimize(brInst, buffer);
                    }
                }
            }
            function.getBasicBlocks().removeIf(block -> {
                if (buffer.contains(block)) {
                    block.setDeleted(true);
                    block.getInstructions().forEach(Instruction::removeOperands);
                    return true;
                }
                return false;
            });
        }
        SurplusBlock.build(module);
        CodeRemoval.run(module);
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

    private static void brOptimize(BrInst brInst, HashSet<BasicBlock> buffer) {
        Value value = brInst.getCondition();
        BasicBlock curBlock = brInst.getBasicBlock();
        if (value instanceof ConstInt constInt) {
            BrInst noCondBr;
            if (constInt.getIntValue() == 0) {
                noCondBr = new BrInst(brInst.getFalseBlock());
                curBlock.deleteForPhi(brInst.getTrueBlock());
                curBlock.getNextBlocks().remove(brInst.getTrueBlock());
                brInst.getTrueBlock().getPrevBlocks().remove(curBlock);
            } else {
                noCondBr = new BrInst(brInst.getTrueBlock());
                curBlock.deleteForPhi(brInst.getTrueBlock());
                curBlock.getNextBlocks().remove(brInst.getFalseBlock());
                brInst.getFalseBlock().getPrevBlocks().remove(curBlock);
            }
            if (brInst.getTrueBlock().getPrevBlocks().isEmpty()) {
                buffer.add(brInst.getTrueBlock());
            }
            if (brInst.getFalseBlock().getPrevBlocks().isEmpty()) {
                buffer.add(brInst.getFalseBlock());
            }
            curBlock.getInstructions().set(curBlock.getInstructions().indexOf(brInst), noCondBr);
            brInst.removeOperands();
        }
    }
}
