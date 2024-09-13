package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.values.Assignable;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class LoadInst extends MemInst implements Assignable {
    public LoadInst(BasicBlock basicBlock, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), Operator.LOAD, basicBlock);
        addOperand(pointer);
        setName("%" + valueIdCount);
        valueIdCount++;
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return getName() + " = load " + getType() + ", " +
                getPointer().getType() + " " + getPointer().getName();
    }
}
