package IR.values.instructions.terminator;

import IR.types.VoidType;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

public class BrInst extends TerminatorInst {
    public BrInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        super(VoidType.voidType, Operator.br, basicBlock);
        addOperand(trueBlock);
    }

    public BrInst(BasicBlock basicBlock, BasicBlock trueBlock, BasicBlock falseBlock, Value cond) {
        super(VoidType.voidType, Operator.br, basicBlock);
        addOperand(cond);
        addOperand(trueBlock);
        addOperand(falseBlock);
    }

    public boolean noneCond() {
        return getOperands().size() == 1;
    }

    public BasicBlock getTrueBlock() {
        if (noneCond()) {
            return (BasicBlock) getOperands().get(0);
        } else {
            return (BasicBlock) getOperands().get(1);
        }
    }

    public void setTrueBLock(BasicBlock trueBlock) {
        getOperands().set(1, trueBlock);
    }

    public BasicBlock getFalseBlock() {
        //assert !noneCond();
        return (BasicBlock) getOperands().get(2);
    }

    public void setFalseBlock(BasicBlock falseBlock) {
        getOperands().set(2, falseBlock);
    }

    public Value getCond() {
        //assert !noneCond();
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        if (noneCond()) {
            return String.format("br label %%%s", getTrueBlock().getName());
        } else {
            return String.format("br i1 %s, label %%%s, label %%%s",
                    getCond().getName(), getTrueBlock().getName(), getFalseBlock().getName());
        }

    }
}
