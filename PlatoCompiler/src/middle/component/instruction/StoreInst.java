package middle.component.instruction;

import middle.component.model.Value;
import middle.component.type.IntegerType;

public class StoreInst extends Instruction {
    public StoreInst(Value pointer, Value storedValue) {
        super("", IntegerType.VOID, OperatorType.STORE);
        addOperand(pointer);
        addOperand(storedValue);
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    public Value getStoredValue() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return "store "
                + getStoredValue().getValueType() + " "
                + getStoredValue().getName() + ", " +
                getPointer().getValueType() + " "
                + getPointer().getName();
    }
}