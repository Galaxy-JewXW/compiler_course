package middle.component.instruction.io;

import middle.TableManager;
import middle.component.Function;
import middle.component.instruction.OperatorType;
import middle.component.type.IntegerType;
import middle.symbol.FuncSymbol;

public class GetcharInst extends IOInst {
    public GetcharInst() {
        super(IntegerType.i32, OperatorType.IO);
    }

    @Override
    public String toString() {
        return getName() + " = call i32 @getchar()";
    }

    @Override
    public Function getCalledFunction() {
        FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance1().
                getSymbol("getchar");
        return funcSymbol.getLlvmValue();
    }

    @Override
    public String getCallee() {
        return "@getchar()";
    }
}
