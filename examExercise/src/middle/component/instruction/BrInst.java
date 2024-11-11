package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.IntegerType;

public class BrInst extends Instruction implements Terminator {
    private final boolean isConditional;

    // 有条件跳转
    public BrInst(Value condition, BasicBlock trueBlock,
                  BasicBlock falseBlock) {
        super("", IntegerType.VOID, OperatorType.BR);
        addOperand(condition);
        addOperand(trueBlock);
        addOperand(falseBlock);
        isConditional = true;
    }

    // 无条件跳转
    public BrInst(BasicBlock targetBlock) {
        super("", IntegerType.VOID, OperatorType.BR);
        addOperand(targetBlock);
        isConditional = false;
    }

    public boolean isConditional() {
        return isConditional;
    }

    public Value getCondition() {
        if (getOperands().size() == 3) {
            return getOperands().get(0);
        } else if (getOperands().size() == 1) {
            return null;
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    public BasicBlock getTrueBlock() {
        if (getOperands().size() == 3) {
            return (BasicBlock) getOperands().get(1);
        } else if (getOperands().size() == 1) {
            return (BasicBlock) getOperands().get(0);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    public void setTrueBlock(BasicBlock trueBlock) {
        getOperands().set(1, trueBlock);
    }

    public BasicBlock getFalseBlock() {
        if (getOperands().size() == 3) {
            return (BasicBlock) getOperands().get(2);
        } else if (getOperands().size() == 1) {
            return null;
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    public void setFalseBlock(BasicBlock falseBlock) {
        getOperands().set(2, falseBlock);
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public String toString() {
        if (getOperands().size() == 3) {
            return "br i1 " + getCondition().getName()
                    + ", label %" + getTrueBlock().getName()
                    + ", label %" + getFalseBlock().getName();
        } else if (getOperands().size() == 1) {
            return "br label %" + getTrueBlock().getName();
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }

    }
}
