package middle.component.instruction;

import middle.component.model.Value;
import middle.component.type.IntegerType;

public class RetInst extends Instruction implements Terminator {
    public RetInst(Value returnValue) {
        super("", IntegerType.VOID, OperatorType.RET);
        if (returnValue != null) {
            addOperands(returnValue);
        }
    }

    public Value getReturnValue() {
        if (getOperands().isEmpty()) {
            return null;
        } else {
            return getOperands().get(0);
        }
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public String toString() {
        if (getReturnValue() != null) {
            return "ret " + getReturnValue().getValueType()
                    + " " + getReturnValue().getName();
        } else {
            return "ret void";
        }
    }
}
