package middle.instructions;

import middle.BasicBlock;
import middle.model.User;
import middle.types.ValueType;

public class Instruction extends User {
    private OperatorType operatorType;
    private BasicBlock basicBlock;

    public Instruction(ValueType valueType, OperatorType operatorType, BasicBlock basicBlock) {
        super("", valueType);
        this.operatorType = operatorType;
        this.basicBlock = basicBlock;
        basicBlock.addInstruction(this);
    }

    public Instruction(ValueType valueType, OperatorType operatorType) {
        super("", valueType);
        this.operatorType = operatorType;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public BasicBlock getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(BasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }
}
