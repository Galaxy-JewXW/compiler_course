package llvm.values.instructions;

import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.instructions.Instruction;
import llvm.values.instructions.Operator;

public class MemInst extends Instruction {
    public MemInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }

    public MemInst(Type type, Operator op) {
        super(type, op);
    }
}
