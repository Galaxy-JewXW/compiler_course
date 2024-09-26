package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.IntegerType;

public class RetInst extends Instruction implements Terminator {
    public RetInst(Value returnValue) {
        super("", IntegerType.VOID, OperatorType.RET);
        if (returnValue != null) {
            addOperand(returnValue);
        }
    }

    public RetInst(Value returnValue, BasicBlock block) {
        super("", IntegerType.VOID, OperatorType.RET, block);
        if (returnValue != null) {
            addOperand(returnValue);
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
