package frontend.symbol;

import middle.component.Function;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private final ArrayList<FuncParam> funcParams;
    private Function llvmValue = null;

    public FuncSymbol(String name, SymbolType returnType, ArrayList<FuncParam> funcParams) {
        super(name, returnType);
        this.funcParams = funcParams;
    }

    public ArrayList<FuncParam> getFuncParams() {
        return funcParams;
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
                ", funcParams=" + funcParams +
                ", llvmValue=" + llvmValue + '}';
    }
}
