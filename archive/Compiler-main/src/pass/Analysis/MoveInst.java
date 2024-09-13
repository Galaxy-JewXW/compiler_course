package pass.Analysis;

import IR.types.VoidType;
import IR.values.Value;
import IR.values.instructions.Operator;
import IR.values.instructions.men.MemInst;

public class MoveInst extends MemInst {
    private Value target;
    private Value value;

    public MoveInst(Value target, Value val) {
        super(VoidType.voidType, Operator.MOVE);
        setName(target.getName());
        addOperand(val);
        this.target = target;
        this.value = val;
    }

    public Value getVal() {
        return value;
    }

    public Value getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "move " + getTarget().getName() + " <- " + getVal().getName();
    }
}
