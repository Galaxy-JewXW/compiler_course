package middle.instructions;

import middle.BasicBlock;
import middle.types.PointerType;
import middle.types.ValueType;

public class AllocInst extends MemInst {
    private final ValueType allocType;

    public AllocInst(ValueType allocType, BasicBlock basicBlock) {
        super(new PointerType(allocType), OperatorType.ALLOC, basicBlock);
        setName("%" + valueIdCount++);
        this.allocType = allocType;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + allocType;
    }
}
