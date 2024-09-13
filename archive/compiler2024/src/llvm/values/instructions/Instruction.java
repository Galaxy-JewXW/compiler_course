package llvm.values.instructions;

import llvm.types.ValueType;
import llvm.values.BasicBlock;
import llvm.values.User;

public class Instruction extends User {
    private final Operator op;
    private BasicBlock basicBlock;

    public Instruction(ValueType type, Operator op, BasicBlock basicBlock) {
        super("", type);
        this.op = op;
        this.basicBlock = basicBlock;
        basicBlock.addInstruction(this);
    }

    public Instruction(ValueType type, Operator op) {
        super("", type);
        this.op = op;
    }

    public Operator getOp() {
        return op;
    }

    public BasicBlock getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(BasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }
}
