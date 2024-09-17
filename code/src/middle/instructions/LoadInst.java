package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;
import middle.types.Assignable;
import middle.types.PointerType;

public class LoadInst extends MemInst implements Assignable {
    public LoadInst(BasicBlock basicBlock, Value pointer) {
        super(((PointerType) pointer.getValueType()).getTargetType(), OperatorType.LOAD, basicBlock);
        addOperand(pointer);
        setName("%" + valueIdCount++);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return getName() + " = load " + getValueType() + ", " +
                getPointer().getValueType() + " " + getPointer().getName();
    }
}
