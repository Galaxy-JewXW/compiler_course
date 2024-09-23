package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.PointerType;

public class LoadInst extends Instruction {
    public LoadInst(String name, Value pointer) {
        super(name, ((PointerType) pointer.getValueType()).getTargetType(),
                OperatorType.LOAD);
        addOperands(pointer);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public String toString() {
        return getName() + " = load " + getValueType() + ", "
                + getPointer().getValueType() + " " + getPointer().getName();
    }
}
