package error;

import java.util.ArrayList;

public class FuncSymbol implements Symbol {
    private final String name;
    private final FuncParam.Type type;
    private final ArrayList<FuncParam> funcParams;

    public FuncSymbol(String name, FuncParam.Type type, ArrayList<FuncParam> funcParams) {
        this.name = name;
        this.type = type;
        this.funcParams = funcParams;
    }

    public ArrayList<FuncParam> getParams() {
        return funcParams;
    }

    @Override
    public FuncParam.Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
