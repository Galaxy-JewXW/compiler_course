package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.types.ValueType;
import llvm.values.BasicBlock;

public class AllocInstruction extends MemoryInstruction {
    private final ValueType allocType;

    public AllocInstruction(ValueType allocType, BasicBlock basicBlock) {
        super(new PointerType(allocType), Operator.ALLOC, basicBlock);
        setName("%" + valueCnt);
        valueCnt++;
        this.allocType = allocType;
    }

    public ValueType getAllocType() {
        return allocType;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + allocType;
    }
}
