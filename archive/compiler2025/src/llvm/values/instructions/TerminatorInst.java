package llvm.values.instructions;

import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.instructions.Instruction;
import llvm.values.instructions.Operator;

public class TerminatorInst extends Instruction {

    public TerminatorInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
