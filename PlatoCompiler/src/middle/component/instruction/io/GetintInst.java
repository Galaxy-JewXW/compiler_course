package middle.component.instruction.io;

import middle.TableManager;
import middle.component.Function;
import middle.component.instruction.OperatorType;
import middle.component.type.IntegerType;
import middle.symbol.FuncSymbol;

public class GetintInst extends IOInst {
    public GetintInst() {
        super(IntegerType.i32, OperatorType.IO);
    }

    @Override
    public String toString() {
        return getName() + " = call i32 @getint()";
    }

    @Override
    public Function getCalledFunction() {
        FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance1().
                getSymbol("getint");
        return funcSymbol.getLlvmValue();
    }

    @Override
    public String getCallee() {
        return "@getint()";
    }
}
