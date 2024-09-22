package frontend.symbol;

import middle.component.Function;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private VarType returnType;
    private ArrayList<VarType> paramTypes;
    private ArrayList<Integer> paramDims;
    private Function value = null;

    public FuncSymbol(String name, VarType varType) {
        super(name, SymbolType.FUNCTION);
        returnType = varType;
        paramTypes = null;
        paramDims = null;
    }

    public FuncSymbol(String name, VarType varType,
                      ArrayList<VarType> paramTypes,
                      ArrayList<Integer> paramDims) {
        this(name, varType);
        this.paramTypes = paramTypes;
        this.paramDims = paramDims;
    }

    public void setParam(ArrayList<VarType> paramTypes,
                         ArrayList<Integer> paramDims) {
        this.paramTypes = paramTypes;
        this.paramDims = paramDims;
    }

    public Function getValue() {
        return value;
    }

    public void setValue(Function value) {
        this.value = value;
    }
}
