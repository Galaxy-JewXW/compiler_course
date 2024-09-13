package IR.values.instructions.men;

import IR.types.PointerType;
import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.instructions.Operator;

public class AllocInst extends MemInst {
    private final Type allocType;

    public AllocInst(Type allocType, BasicBlock basicBlock) {
        super(new PointerType(allocType), Operator.Alloc, basicBlock);
        setName("%" + valNumber++);
        this.allocType = allocType;
    }

    public Type getAllocType() {
        return allocType;
    }

    @Override
    public String toString() {
        return String.format("%s = alloca %s", getName(), allocType);
    }
}
