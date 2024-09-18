package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.types.ValueType;

public class MemInst extends Instruction {
    public MemInst(ValueType valueType, OperatorType operatorType, BasicBlock basicBlock) {
        super(valueType, operatorType, basicBlock);
    }

    public MemInst(ValueType valueType, OperatorType operatorType) {
        super(valueType, operatorType);
    }
}
