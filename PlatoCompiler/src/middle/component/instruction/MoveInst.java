package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.IntegerType;

public class MoveInst extends Instruction {
    public MoveInst(Value toValue, Value fromValue, BasicBlock block) {
        super("", IntegerType.VOID, OperatorType.MOVE);
        addOperand(toValue);
        addOperand(fromValue);
        setBasicBlock(block);
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    public Value getToValue() {
        return getOperands().get(0);
    }

    public Value getFromValue() {
        return getOperands().get(1);
    }

    public void setFromValue(Value fromValue) {
        if (fromValue != null) {
            fromValue.addUse(this);
        }
        getOperands().set(1, fromValue);
    }

    public String toString() {
        return "move " + getFromValue().getName() +
                " -> " + getToValue().getName();
    }
}
