package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;
import middle.types.ValueType;

public class ZextInst extends Instruction {
    public ZextInst(Value value, BasicBlock basicBlock, ValueType targetType) {
        super(targetType, OperatorType.ZEXT, basicBlock);
        addOperand(value);
        setName("%" + valueIdCount++);
    }

    public Value getValue() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return getName() + " = zext " + getValue().getValueType()
                + " " + getValue().getName() + " to " + getValueType();
    }
}
