package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.VoidType;

public class BrInst extends TerminatorInst {
    public BrInst(BasicBlock block, BasicBlock targetBlock) {
        super(VoidType.VOID, OperatorType.BR, block);
        addOperand(targetBlock);
    }

    public BrInst(BasicBlock block, BasicBlock trueBlock, BasicBlock falseBlock, Value cond) {
        super(VoidType.VOID, OperatorType.BR, block);
        addOperand(cond);
        addOperand(trueBlock);
        addOperand(falseBlock);
    }

    public BasicBlock getTrueBlock() {
        if (getOperands().size() == 1) {
            return (BasicBlock) getOperands().get(0);
        } else {
            return (BasicBlock) getOperands().get(1);
        }
    }

    public BasicBlock getFalseBlock() {
        if (getOperands().size() == 3) {
            return (BasicBlock) getOperands().get(2);
        } else {
            return null;
        }
    }

    public Value getCond() {
        if (getOperands().size() == 3) {
            return getOperands().get(0);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (getOperands().size() == 1) {
            return "br label %" + getTrueBlock().getName();
        } else {
            return "br i1 " + getCond().getName() + ", label %" + getTrueBlock().getName() +
                    ", label %" + getFalseBlock().getName();
        }
    }
}
