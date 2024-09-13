package llvm.values.instructions;

import llvm.values.BasicBlock;
import llvm.values.Value;

public class StoreInst extends MemInst {

    public StoreInst(BasicBlock basicBlock, Value storeValue, Value pointer) {
        super(storeValue.getType(), Operator.STORE, basicBlock);
        addOperand(storeValue);
        addOperand(pointer);
    }

    public Value getStoreValue() {
        return getOperands().get(0);
    }

    public Value getPointer() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return "store " + getStoreValue().getType() + " " + getStoreValue().getName() + ", " +
                getPointer().getType() + " " + getPointer().getName();
    }
}
