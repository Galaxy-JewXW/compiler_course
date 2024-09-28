package pass;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BinaryInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;

// 额外对乘除计算进行优化
public class FixMD {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions
                        = new ArrayList<>(basicBlock.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof BinaryInst binaryInst) {
                        if (binaryInst.getOpType() == OperatorType.MUL
                                || binaryInst.getOpType() == OperatorType.SDIV) {
                            fixMulDiv(binaryInst);
                        }
                    }
                }
            }
        }
    }

    private static void fixMulDiv(BinaryInst binaryInst) {
        int constCnt = 0;
        for (Value value : binaryInst.getOperands()) {
            if (value instanceof ConstInt) {
                constCnt++;
            }
        }
        if (constCnt != 1) {
            return;
        }
        int intValue;
        Value value;
        if (binaryInst.getOperand1() instanceof ConstInt constInt) {
            intValue = constInt.getIntValue();
            value = binaryInst.getOperand2();
        } else if (binaryInst.getOperand2() instanceof ConstInt constInt) {
            intValue = constInt.getIntValue();
            value = binaryInst.getOperand1();
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
        if (intValue == -1) {
            BinaryInst inst = new BinaryInst(OperatorType.SUB,
                    new ConstInt(IntegerType.i32, 0), value);
            inst.setBasicBlock(binaryInst.getBasicBlock());
            binaryInst.replaceByNewValue(inst);
            binaryInst.getBasicBlock().getInstructions().set(
                    binaryInst.getBasicBlock().getInstructions().indexOf(binaryInst), inst);
            binaryInst.getBasicBlock().getInstructions().remove(binaryInst);
            binaryInst.deleteUse();
        }
    }
}
