package middle.instructions;

import middle.BasicBlock;
import middle.types.ValueType;

public class TerminatorInst extends Instruction {
    public TerminatorInst(ValueType valueType, OperatorType operatorType, BasicBlock basicBlock) {
        super(valueType, operatorType, basicBlock);
    }
}
