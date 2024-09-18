package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.Assignable;
import middle.component.types.IntegerType;

public class BinaryInst extends Instruction implements Assignable {
    public BinaryInst(BasicBlock basicBlock, OperatorType operatorType, Value lValue, Value rValue) {
        super(lValue.getValueType(), operatorType, basicBlock);
        if (isLogical()) {
            // 如果该指令为逻辑运算，返回值为位数为1
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
                getLValue().getName() + ", " + getRValue().getName();
    }
}
