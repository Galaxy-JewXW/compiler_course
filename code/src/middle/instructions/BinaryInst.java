package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;
import middle.types.Assignable;
import middle.types.IntegerType;

public class BinaryInst extends Instruction implements Assignable {
    public BinaryInst(BasicBlock basicBlock, OperatorType operatorType, Value lValue, Value rValue) {
        super(lValue.getValueType(), operatorType, basicBlock);
        if (isLogical()) {
            setValueType(IntegerType.i1);
        }
        addOperand(lValue);
        addOperand(rValue);
        setName("%" + valueIdCount++);
    }

    public boolean isLogical() {
        OperatorType opType = getOperatorType();
        return opType == OperatorType.ICMP_EQ || opType == OperatorType.ICMP_NE ||
                opType == OperatorType.ICMP_SLE || opType == OperatorType.ICMP_SLT ||
                opType == OperatorType.ICMP_SGE || opType == OperatorType.ICMP_SGT;
    }

    public Value getLValue() {
        return getOperands().get(0);
    }

    public Value getRValue() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return getName() + " = " + getOperatorType().toString() + " " +
                getLValue().getValueType() + " " +
                getLValue().getName() + " " + getRValue().getName();
    }
}
