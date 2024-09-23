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
        StringBuilder sb = new StringBuilder("FuncSymbol{");
        sb.append("name='").append(super.getName()).append('\'');
        sb.append(", returnType=").append(super.getType());
        sb.append(", funcParams=[");
        for (FuncParam fp : funcParams) {
            sb.append(fp.toString());
            sb.append(", ");
        }
        sb.append("], llvmValue=").append(llvmValue).append('}');
        return sb.toString();
    }
}
