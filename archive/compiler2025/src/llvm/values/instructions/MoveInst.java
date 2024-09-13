package llvm.values.instructions;

import llvm.types.VoidType;
import llvm.values.Value;

public class MoveInst extends MemInst {
    private final Value target;
    private final Value source;

    public MoveInst(Value target, Value source) {
        super(VoidType.voidType, Operator.MOVE);
        this.target = target;
        this.source = source;
        setName(target.getName());
        addOperand(source);
    }

    public Value getTarget() {
        return target;
    }

    public Value getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "move " + target.getName() + " <- " + source.getName();
    }
}
