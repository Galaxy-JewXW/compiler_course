package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;
import middle.types.ValueType;

public class TruncInst extends Instruction {
    public TruncInst(Value value, BasicBlock basicBlock, ValueType targetType) {
        super(targetType, OperatorType.TRUNC, basicBlock);
        addOperand(value);
        setName("%" + valueIdCount++);
    }

    public Value getValue() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return getName() + " = trunc " + getValue().getValueType()
                + " " + getValue().getName() + " to " + getValueType();
    }
}
