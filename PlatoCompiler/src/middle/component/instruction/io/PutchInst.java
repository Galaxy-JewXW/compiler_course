package middle.component.instruction.io;

import middle.component.BasicBlock;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;

public class PutchInst extends IOInst {
    public PutchInst(String name, Value target) {
        super(name, IntegerType.VOID, OperatorType.IO);
        addOperands(target);
    }

    public Value getTarget() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return "call void @putch(i8 " + getTarget().getName() + ")";
    }
}
