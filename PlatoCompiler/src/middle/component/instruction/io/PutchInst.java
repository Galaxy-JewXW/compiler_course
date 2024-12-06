package middle.component.instruction.io;

import middle.TableManager;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.symbol.FuncSymbol;

public class PutchInst extends IOInst implements OutputInst {
    public PutchInst(Value target) {
        super("", IntegerType.VOID, OperatorType.IO);
        addOperand(target);
    }

    public Value getTarget() {
        return getOperands().get(0);
    }

    @Override
    public boolean constContent() {
        return getTarget() instanceof ConstInt;
    }

    @Override
    public String getConstContent() {
        if (constContent()) {
            ConstInt c = (ConstInt) getTarget();
            return String.valueOf((char) c.getIntValue());
        }
        return null;
    }

    @Override
    public String toString() {
        return "call void @putch(i8 " + getTarget().getName() + ")";
    }

    @Override
    public Function getCalledFunction() {
        FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance1().
                getSymbol("putch");
        return funcSymbol.getLlvmValue();
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public String getCallee() {
        return "@putch(i8 " + getTarget().getName() + ")";
    }
}
