package IR.values.instructions;

import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.User;
import IR.values.Value;

public class Instruction extends User {
    private Operator op;
    private BasicBlock basicBlock;

    public Instruction(Type type, Operator op, BasicBlock basicBlock) {
        super("", type);
        this.op = op;
        this.basicBlock = basicBlock;
        basicBlock.addInstruction(this);
    }

    public Instruction(Type type, Operator op) {
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

    public void refreshName() {
        if (!getName().isEmpty()) {
            setName("%" + valNumber++);
        }
    }
}
