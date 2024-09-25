package middle.component.instruction;

import middle.IRData;
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
        // 设置前驱后继关系
        IRData.getCurrentBlock().addNextBlock(trueBlock);
        IRData.getCurrentBlock().addNextBlock(falseBlock);
        trueBlock.addPrevBlock(IRData.getCurrentBlock());
        falseBlock.addPrevBlock(IRData.getCurrentBlock());
    }

    // 无条件跳转
    public BrInst(BasicBlock targetBlock) {
        super("", IntegerType.VOID, OperatorType.BR);
        addOperand(targetBlock);
        isConditional = false;
        // 设置前驱后继关系
        IRData.getCurrentBlock().addNextBlock(targetBlock);
        targetBlock.addPrevBlock(IRData.getCurrentBlock());
    }

    // 指定了指令所属的block
    // 指定block时，不自动加入block，也不自动更新前后驱关系
    public BrInst(BasicBlock block, BasicBlock targetBlock) {
        super("", IntegerType.VOID, OperatorType.BR, block);
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
