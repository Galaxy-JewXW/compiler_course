package llvm.values.instructions;

import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class BrInstruction extends TerminatorInstruction {
    public BrInstruction(BasicBlock basicBlock, BasicBlock trueBlock) {
        super(new VoidType(), Operator.BR, basicBlock);
        addOperand(trueBlock);
    }

    public BrInstruction(BasicBlock basicBlock, BasicBlock trueBlock, BasicBlock falseBlock, Value cond) {
        super(new VoidType(), Operator.BR, basicBlock);
        addOperand(cond);
        addOperand(trueBlock);
        addOperand(falseBlock);
    }

    public boolean noCond() {
        return getOperands().size() == 1;
    }

    public BasicBlock getTrueBlock() {
        if (noCond()) {
            return (BasicBlock) getOperands().get(0);
        } else {
            return (BasicBlock) getOperands().get(1);
        }
    }

    public void setTrueBLock(BasicBlock trueBlock) {
        getOperands().set(1, trueBlock);
    }

    public BasicBlock getFalseBlock() {
        return (BasicBlock) getOperands().get(2);
    }

    public void setFalseBlock(BasicBlock falseBlock) {
        getOperands().set(2, falseBlock);
    }

    public Value getCond() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        if (noCond()) {
            return "br label %" + getTrueBlock().getName();
        } else {
            return "br i1 " + getCond().getName() + ", label %" + getTrueBlock().getName() +
                    ", label %" + getFalseBlock().getName();
        }
    }

}
