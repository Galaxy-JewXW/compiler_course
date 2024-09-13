package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.values.Assignable;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class LoadInstruction extends MemoryInstruction implements Assignable {
    public LoadInstruction(BasicBlock basicBlock, Value value) {
        super(((PointerType) value.getType()).getPointToType(),
                Operator.LOAD, basicBlock);
        setName("%" + valueCnt);
        valueCnt++;
        addOperand(value);
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
