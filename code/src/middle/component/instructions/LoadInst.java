package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.Assignable;
import middle.component.types.PointerType;

/* 加载指令类
 * %3 = load i32, i32* %2
 * %2是指向某个存有i32的内存的指针
 * 取值之后，%3是一个数字，而非一个指针
 */
public class LoadInst extends MemInst implements Assignable {
    public LoadInst(BasicBlock basicBlock, Value pointer) {
        super(((PointerType) pointer.getValueType()).getTargetType(), OperatorType.LOAD, basicBlock);
        addOperand(pointer);
        setName("%" + allocIdCount());
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return getName() + " = load " + getValueType() + ", " +
                getPointer().getValueType() + " " + getPointer().getName();
    }
}
