package error.symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<FuncParam> funcParams;

    public FuncSymbol(String name, Type type, ArrayList<FuncParam> funcParams) {
        super(name, type);
        this.funcParams = funcParams;
    }

    public ArrayList<FuncParam> getFuncParams() {
        return funcParams;
    }
}
