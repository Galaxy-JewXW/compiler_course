package middle.component.instruction.io;

import middle.component.BasicBlock;
import middle.component.instruction.OperatorType;
import middle.component.type.IntegerType;

public class GetcharInst extends IOInst {
    public GetcharInst(String name) {
        super(name, IntegerType.i32, OperatorType.IO);
    }

    @Override
    public String toString() {
        return getName() + " = call i32 @getchar()";
    }
}
