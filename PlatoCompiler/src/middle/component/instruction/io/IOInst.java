package middle.component.instruction.io;

import middle.component.BasicBlock;
import middle.component.instruction.Instruction;
import middle.component.instruction.OperatorType;
import middle.component.type.ValueType;

public class IOInst extends Instruction {
    public IOInst(String name, ValueType type, OperatorType opType) {
        super(name, type, opType);
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }
}
