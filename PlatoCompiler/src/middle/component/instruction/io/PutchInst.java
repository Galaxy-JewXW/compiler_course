package middle.component.instruction.io;

import frontend.TableManager;
import frontend.symbol.FuncSymbol;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.instruction.OperatorType;
import middle.component.model.Value;
import middle.component.type.IntegerType;

public class PutchInst extends IOInst {
    public PutchInst(Value target) {
        super("", IntegerType.VOID, OperatorType.IO);
        addOperand(target);
    }

    public PutchInst(Value target, BasicBlock block) {
        super("", IntegerType.VOID, OperatorType.IO, block);
        addOperand(target);
    }

    public Value getTarget() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return "call void @putch(i8 " + getTarget().getName() + ")";
    }

    @Override
    public Function getCalledFunction() {
        FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance().
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
