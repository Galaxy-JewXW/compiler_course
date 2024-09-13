package IR.values.instructions.terminator;

import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.instructions.Instruction;
import IR.values.instructions.Operator;

public class TerminatorInst extends Instruction {

    public TerminatorInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
