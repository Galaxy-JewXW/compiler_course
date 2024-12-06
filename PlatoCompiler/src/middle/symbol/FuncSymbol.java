package middle.symbol;

import middle.component.Function;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private final ArrayList<ParamSymbol> paramSymbols;
    private Function llvmValue = null;

    public FuncSymbol(String name, SymbolType returnType, ArrayList<ParamSymbol> paramSymbols) {
        super(name, returnType);
        this.paramSymbols = paramSymbols;
    }

    public ArrayList<ParamSymbol> getFuncParams() {
        return paramSymbols;
    }

    public Function getLlvmValue() {
        return llvmValue;
    }

    public void setLlvmValue(Function llvmValue) {
        this.llvmValue = llvmValue;
    }

    @Override
    public String toString() {
        return "FuncSymbol{" + "name='" + super.getName() + '\'' +
                ", returnType=" + super.getType() +
                ", paramSymbols=" + paramSymbols +
                ", llvmValue=" + llvmValue + '}';
    }
}
