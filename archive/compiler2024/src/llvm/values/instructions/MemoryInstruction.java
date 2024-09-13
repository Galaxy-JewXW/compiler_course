package llvm.values.instructions;

import llvm.types.ValueType;
import llvm.values.BasicBlock;

public class MemoryInstruction extends Instruction {
    public MemoryInstruction(ValueType type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }

    public MemoryInstruction(ValueType type, Operator op) {
        super(type, op);
    }
}
