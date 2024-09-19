package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.ValueType;

public class ZextInst extends Instruction {
    public ZextInst(Value value, BasicBlock basicBlock, ValueType targetType) {
        super(targetType, OperatorType.ZEXT, basicBlock);
        addOperand(value);
        setName("%" + allocIdCount());
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
