package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.types.ValueType;

public class TerminatorInst extends Instruction {
    public TerminatorInst(ValueType valueType, OperatorType operatorType, BasicBlock basicBlock) {
        super(valueType, operatorType, basicBlock);
    }
}
