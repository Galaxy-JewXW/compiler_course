package middle.component.instruction;

import middle.component.model.Value;
import middle.component.type.ValueType;

public class TruncInst extends Instruction {
    public TruncInst(Value value, ValueType targetType) {
        super(targetType, OperatorType.ZEXT);
        addOperands(value);
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
        return getName() + " = trunc " + getOriginValue().getValueType() + " "
                + getOriginValue().getName() + " to " +
                getValueType();
    }
}
