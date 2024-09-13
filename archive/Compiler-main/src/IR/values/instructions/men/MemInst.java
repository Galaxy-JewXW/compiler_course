package IR.values.instructions.men;

import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.instructions.Instruction;
import IR.values.instructions.Operator;

public class MemInst extends Instruction {
    public MemInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }

    public MemInst(Type type, Operator op) {
        super(type, op);
    }
}
