package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.types.ArrayType;
import middle.component.types.PointerType;
import middle.component.types.ValueType;

import java.util.ArrayList;

/* 指针运算指令类
 * 我们有这样的一个例子：int a[10]; int c = a[2];
 * 对应的LLVM IR为：
 * %a = alloca [10 x i32]
 * %elementPtr = getelementptr inbounds [10 x i32], [10 x i32]* %a, i32 0, i32 2
 * 第一个i32 0是数组基地址偏移量，第二个i32 2是数组元素偏移量
 * 通过gep指令计算之后，%elementPtr就是a[2]的地址，也就是说%elementPtr也是一个指针
 * 接下来使用load指令从某个地址中取值
 * %c = load i32, i32* %elementPtr
 */
public class GepInst extends MemInst {
    private final ValueType targetType;

    private static ValueType preWork(Value pointer) {
        ValueType type = ((PointerType) pointer.getValueType()).getTargetType();
        type = ((ArrayType) type).getElementType();
        return type;
    }

    public GepInst(Value pointer, ArrayList<Value> indexes, BasicBlock basicBlock) {
        super(new PointerType(preWork(pointer)), OperatorType.GEP, basicBlock);
        addOperand(pointer);
        this.targetType = ((PointerType) pointer.getValueType()).getTargetType();
        for (Value index : indexes) {
            addOperand(index);
        }
        setName("%" + allocIdCount());
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    private String getIndex(Value value) {
        return value.getValueType().toString() + ' ' + value.getName();
    }

    public String toString() {
        StringBuilder res = new StringBuilder(getName());
        res.append(" = getelementptr inbounds ");
        res.append(targetType).append(", ");
        res.append(getPointer().getValueType()).append(" ");
        res.append(getPointer().getName()).append(", ");
        res.append(getIndex(getOperands().get(1)));
        for (int i = 2; i < getOperands().size(); i++) {
            res.append(", ").append(getIndex(getOperands().get(i)));
        }
        return res.toString();
    }
}
