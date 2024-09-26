package middle.component.instruction;

import middle.component.model.Value;
import middle.component.type.ValueType;

public class ZextInst extends Instruction {
    public ZextInst(Value value, ValueType targetType) {
        super(targetType, OperatorType.ZEXT);
        addOperand(value);
    }

    public Value getOriginValue() {
        return getOperands().get(0);
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public String toString() {
        return getName() + " = zext " + getOriginValue().getValueType() + " "
                + getOriginValue().getName() + " to " +
                getValueType();
    }
}
