package middle.component.instruction.io;

import middle.TableManager;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.symbol.FuncSymbol;

public class PutintInst extends IOInst implements OutputInst {
    public PutintInst(Value target) {
        super("", IntegerType.VOID, OperatorType.IO);
        addOperand(target);
    }

    public Value getTarget() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return "call void @putint(i32 " + getTarget().getName() + ")";
    }

    @Override
    public boolean constContent() {
        return getTarget() instanceof ConstInt;
    }

    @Override
    public String getConstContent() {
        if (constContent()) {
            return getTarget().getName();
        }
        return null;
    }

    @Override
    public Function getCalledFunction() {
        FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance1().
                getSymbol("putint");
        return funcSymbol.getLlvmValue();
    }

    @Override
    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public String getCallee() {
        return "@putint(i32 " + getTarget().getName() + ")";
    }
}
