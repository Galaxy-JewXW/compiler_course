package middle.component.instruction.io;

import middle.TableManager;
import middle.component.ConstString;
import middle.component.Function;
import middle.component.instruction.OperatorType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.symbol.FuncSymbol;

public class PutstrInst extends IOInst implements OutputInst {
    // constString是printf中需要打印的句子
    // 单个字符也按照string处理，主要是懒
    private final ConstString constString;

    public PutstrInst(ConstString constString) {
        super("", IntegerType.VOID, OperatorType.IO);
        this.constString = constString;
    }

    public ConstString getConstString() {
        return constString;
    }

    @Override
    public boolean constContent() {
        return true;
    }

    @Override
    public String getConstContent() {
        if (constContent()) {
            return constString.getContent();
        }
        return null;
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

    @Override
    public Function getCalledFunction() {
        FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance1().
                getSymbol("putstr");
        return funcSymbol.getLlvmValue();
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public String getCallee() {
        PointerType pointerType = (PointerType) constString.getValueType();
        return "@putstr(i8* getelementptr inbounds (" +
                pointerType.getTargetType() + ", " +
                pointerType + " " +
                constString.getName() +
                ", i64 0, i64 0))";
    }
}
