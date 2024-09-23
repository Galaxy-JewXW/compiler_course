package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.ValueType;

public class ZextInst extends Instruction {
    public ZextInst(String name, Value value, ValueType targetType) {
        super(name, targetType, OperatorType.ZEXT);
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
        return getName() + " = zext " + getOriginValue().getValueType() + " "
                + getOriginValue().getName() + " to " +
                getValueType();
    }
}
