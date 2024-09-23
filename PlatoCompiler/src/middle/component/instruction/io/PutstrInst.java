package middle.component.instruction.io;

import middle.component.BasicBlock;
import middle.component.ConstString;
import middle.component.instruction.OperatorType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;

public class PutstrInst extends IOInst {
    // constString是printf中需要打印的句子
    // 单个字符也按照string处理，主要是懒
    private final ConstString constString;

    public PutstrInst(String name, ConstString constString) {
        super(name, IntegerType.VOID, OperatorType.IO);
        this.constString = constString;
    }

    @Override
    public String toString() {
        PointerType pointerType = (PointerType) constString.getValueType();
        return "call void @putstr(i8* getelementptr inbounds (" +
                pointerType.getTargetType() + ", " +
                pointerType + " " +
                constString.getName() +
                ", i64 0, i64 0))";
    }
}
