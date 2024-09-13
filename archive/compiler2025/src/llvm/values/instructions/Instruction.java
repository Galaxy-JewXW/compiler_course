package llvm.values.instructions;

import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.User;
import llvm.values.Value;

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
            setName("%" + valueIdCount);
            valueIdCount++;
        }
    }

}
