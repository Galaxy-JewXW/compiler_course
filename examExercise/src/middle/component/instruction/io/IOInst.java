package middle.component.instruction.io;

import middle.component.instruction.Call;
import middle.component.instruction.Instruction;
import middle.component.instruction.OperatorType;
import middle.component.type.ValueType;

public abstract class IOInst extends Instruction implements Call {
    public IOInst(ValueType type, OperatorType opType) {
        super(type, opType);
    }

    public IOInst(String name, ValueType type, OperatorType opType) {
        super(name, type, opType);
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }
}
