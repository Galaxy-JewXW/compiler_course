package llvm.values.instructions;

import llvm.types.IntType;
import llvm.values.Assignable;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class BinaryInstruction extends Instruction implements Assignable {
    public BinaryInstruction(BasicBlock basicBlock, Operator op, Value lVal, Value rVal) {
        super(lVal.getType(), op, basicBlock);
        if (isLogical()) {
            setType(new IntType(1));
        }
        addOperand(lVal);
        addOperand(rVal);
        setName("%" + valueCnt);
        valueCnt++;
    }

    public boolean isNumber() {
        Operator op = getOp();
        return op == Operator.ADD || op == Operator.SUB ||
                op == Operator.MUL || op == Operator.SDIV ||
                op == Operator.SREM;
    }

    public boolean isLogical() {
        Operator op = getOp();
        return op == Operator.ICMP_EQ || op == Operator.ICMP_NE ||
                op == Operator.ICMP_SLE || op == Operator.ICMP_SLT ||
                op == Operator.ICMP_SGE || op == Operator.ICMP_SGT;
    }



    public Value getLVal() {
        return getOperands().get(0);
    }

    public Value getRVal() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return getName() + " = " + getOp().toString() + " " +
                getLVal().getType() + " " + getLVal().getName() + ", " +
                getRVal().getName();
    }
}
