package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BasicBlock;

public class AllocInst extends MemInst {
    private final Type allocType;

    public AllocInst(Type allocType, BasicBlock basicBlock) {
        super(new PointerType(allocType), Operator.ALLOC, basicBlock);
        setName("%" + valueIdCount);
        valueIdCount++;
        this.allocType = allocType;
    }

    public Type getAllocType() {
        return allocType;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + allocType;
    }
}
