package middle.component.instruction.io;

import middle.component.BasicBlock;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;

public class PutintInst extends IOInst {
    public PutintInst(String name, Value target, BasicBlock block) {
        super(name, IntegerType.VOID, OperatorType.IO, block);
        addOperands(target);
    }

    public Value getTarget() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return "call void @putint(i32 " + getTarget().getName() + ")";
    }
}
