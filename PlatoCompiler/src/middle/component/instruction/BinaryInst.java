package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

public class BinaryInst extends Instruction {
    private final OperatorType opType;

    private static ValueType getValueType(OperatorType opType) {
        return OperatorType.isLogicalOperator(opType) ? IntegerType.i1 : IntegerType.i32;
    }

    public BinaryInst(OperatorType opType,
                      Value operand1, Value operand2) {
        super(getValueType(opType), opType);
        this.opType = opType;
        addOperands(operand1);
        addOperands(operand2);
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    public Value getOperand1() {
        return getOperands().get(0);
    }

    public Value getOperand2() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return getName() + " = " + opType + " "
                + getOperand1().getValueType() + " " + getOperand1().getName()
                + ", " + getOperand2().getName();
    }
}
