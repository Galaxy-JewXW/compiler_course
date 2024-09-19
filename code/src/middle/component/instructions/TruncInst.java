package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.ValueType;

public class TruncInst extends Instruction {
    public TruncInst(Value value, BasicBlock basicBlock, ValueType targetType) {
        super(targetType, OperatorType.TRUNC, basicBlock);
        addOperand(value);
        setName("%" + allocIdCount());
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
