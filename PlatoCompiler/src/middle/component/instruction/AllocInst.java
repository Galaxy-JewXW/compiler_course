package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

public class AllocInst extends Instruction {
    // alloc指令在栈上分配一个地址空间，返回值是一个指针
    private final ValueType targetType;

    public AllocInst(ValueType targetType) {
        super(new PointerType(targetType), OperatorType.ALLOC);
        this.targetType = targetType;
    }

    public AllocInst(ValueType targetType, BasicBlock block) {
        super(new PointerType(targetType), OperatorType.ALLOC, block);
        this.targetType = targetType;
    }

    public ValueType getTargetType() {
        return targetType;
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + targetType;
    }
}
