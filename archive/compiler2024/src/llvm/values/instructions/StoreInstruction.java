package llvm.values.instructions;

import llvm.values.BasicBlock;
import llvm.values.Value;

public class StoreInstruction extends MemoryInstruction {
    public StoreInstruction(BasicBlock basicBlock, Value value, Value pointer) {
        super(value.getType(), Operator.STORE, basicBlock);
        addOperand(value);
        addOperand(pointer);
    }

    public Value getValue() {
        return getOperands().get(0);
    }

    public Value getPointer() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return "store " + getValue().getType() + " " + getValue().getName() + ", " +
                getPointer().getType() + " " + getPointer().getName();
    }

}
