package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.PointerType;
import middle.component.types.ValueType;

/* 指针分配指令类
 * %1 = alloca i32表示在栈上分配一个i32类型的空间。这个空间的大小是一个 i32（4字节），
 * 并且这个空间的生命周期只存在于当前函数的调用期间。
 * 也就是说，当函数执行完毕，栈空间会被自动释放。
 * alloca 返回的是指向分配内存的指针。在这个例子中，%1是一个指向i32的指针（i32*类型）
 * 粗略的用malloc类比，等价于int *p = (int *)malloc(sizeof(int));
 */
public class AllocInst extends MemInst {
    private final ValueType allocType;

    public AllocInst(ValueType allocType, BasicBlock basicBlock) {
        super(new PointerType(allocType), OperatorType.ALLOC, basicBlock);
        setName("%" + Value.allocIdCount());
        this.allocType = allocType;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + allocType;
    }
}
