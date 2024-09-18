package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;

public class StoreInst extends MemInst {
    public StoreInst(BasicBlock basicBlock, Value storeValue, Value pointer) {
        super(storeValue.getValueType(), OperatorType.STORE, basicBlock);
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
        return "store " + getStoreValue().getValueType() + " " +
                getStoreValue().getName() + ", " +
                getPointer().getValueType() + " " + getPointer().getName();
    }
}
