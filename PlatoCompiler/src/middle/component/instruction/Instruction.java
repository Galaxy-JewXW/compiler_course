package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.User;
import middle.component.type.ValueType;

public abstract class Instruction extends User {
    private OperatorType opType;
    // 指令所处的基本块
    private BasicBlock basicBlock;

    public Instruction(String name, ValueType valueType, OperatorType opType) {
        super(name, valueType);
        this.opType = opType;
        this.basicBlock = null;
    }

    public BasicBlock getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(BasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }

    public abstract boolean hasSideEffect();
}