package frontend.symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private final ArrayList<FuncParam> funcParams;

    public FuncSymbol(String name, SymbolType symbolType, ArrayList<FuncParam> funcParams) {
        super(name, symbolType);
        this.funcParams = funcParams;
    }

    public ArrayList<FuncParam> getFuncParams() {
        return funcParams;
    }
}
