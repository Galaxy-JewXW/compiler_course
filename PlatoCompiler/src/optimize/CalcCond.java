package optimize;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BinaryInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.OperatorType;
import middle.component.instruction.ZextInst;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.Iterator;

public class CalcCond {
    // 先结合比较符号，再结合相等符号
    public static void run(Module module) {
        runRelExp(module);
    }

    private static void runRelExp(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                calcRelExp(basicBlock);
            }
        }
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                calcEqExp(basicBlock);
            }
        }
    }

    private static void calcRelExp(BasicBlock basicBlock) {
        Iterator<Instruction> it = basicBlock.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instruction = it.next();
            if (!(instruction instanceof BinaryInst binaryInst)) {
                continue;
            }
            OperatorType op = binaryInst.getOpType();
            if (op == OperatorType.ICMP_SLE || op == OperatorType.ICMP_SLT
                    || op == OperatorType.ICMP_SGE || op == OperatorType.ICMP_SGT) {
                Value operand1 = binaryInst.getOperand1();
                Value operand2 = binaryInst.getOperand2();
                if (operand1 instanceof ConstInt constInt1
                        && operand2 instanceof ConstInt constInt2) {
                    ConstInt newInt = getCompInt(constInt1, constInt2, op);
                    instruction.replaceByNewValue(newInt);
                    it.remove();
                }
            }
        }
    }

    private static void calcEqExp(BasicBlock basicBlock) {
        Iterator<Instruction> it = basicBlock.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instruction = it.next();
            if (!(instruction instanceof BinaryInst binaryInst)) {
                continue;
            }
            OperatorType op = binaryInst.getOpType();
            if (op == OperatorType.ICMP_EQ || op == OperatorType.ICMP_NE) {
                Value operand1 = binaryInst.getOperand1();
                Value operand2 = binaryInst.getOperand2();
                if (operand1 instanceof ConstInt constInt1
                        && operand2 instanceof ConstInt constInt2) {
                    ConstInt newInt = getEqInt(constInt1, constInt2, op);
                    instruction.replaceByNewValue(newInt);
                    it.remove();
                } else if (operand1 instanceof ConstInt constInt1
                        && operand2 instanceof ZextInst zextInst) {
                    if (zextInst.getOriginValue() instanceof ConstInt constInt2) {
                        ConstInt newInt = getEqInt(constInt1, constInt2, op);
                        instruction.replaceByNewValue(newInt);
                        it.remove();
                    }
                } else if (operand1 instanceof ZextInst zextInst
                        && operand2 instanceof ConstInt constInt2) {
                    if (zextInst.getOriginValue() instanceof ConstInt constInt1) {
                        ConstInt newInt = getEqInt(constInt1, constInt2, op);
                        instruction.replaceByNewValue(newInt);
                        it.remove();
                    }
                } else if (operand1 instanceof ZextInst zextInst1
                        && operand2 instanceof ZextInst zextInst2) {
                    if (zextInst1.getOriginValue() instanceof ConstInt constInt1
                            && zextInst2.getOriginValue() instanceof ConstInt constInt2) {
                        ConstInt newInt = getEqInt(constInt1, constInt2, op);
                        instruction.replaceByNewValue(newInt);
                        it.remove();
                    }
                }
            }
        }
    }

    private static ConstInt getCompInt(ConstInt constInt1, ConstInt constInt2, OperatorType op) {
        int left = constInt1.getIntValue();
        int right = constInt2.getIntValue();
        int ans = switch (op) {
            case ICMP_SLT -> left < right ? 1 : 0;
            case ICMP_SGT -> left > right ? 1 : 0;
            case ICMP_SGE -> left <= right ? 1 : 0;
            case ICMP_SLE -> left >= right ? 1 : 0;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + op);
        };
        return new ConstInt(IntegerType.i1, ans);
    }

    private static ConstInt getEqInt(ConstInt constInt1, ConstInt constInt2,
                                     OperatorType op) {
        int left = constInt1.getIntValue();
        int right = constInt2.getIntValue();
        int ans = switch (op) {
            case ICMP_EQ -> left == right ? 1 : 0;
            case ICMP_NE -> left != right ? 1 : 0;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + op);
        };
        return new ConstInt(IntegerType.i1, ans);
    }
}
