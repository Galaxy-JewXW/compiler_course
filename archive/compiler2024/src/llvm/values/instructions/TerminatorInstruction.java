package llvm.values.instructions;

import llvm.types.ValueType;
import llvm.values.BasicBlock;

public class TerminatorInstruction extends Instruction {
    public TerminatorInstruction(ValueType valueType, Operator op, BasicBlock basicBlock) {
        super(valueType, op, basicBlock);
    }
}
