package error;

import java.util.HashMap;

public class ErrorSymbolTable {
    private final HashMap<String, Symbol> symbolMap = new HashMap<>();
    private final FuncParam.Type type;

    public ErrorSymbolTable(FuncParam.Type type) {
        this.type = type;
    }

    public boolean contains(String name) {
        return symbolMap.containsKey(name);
    }

    public void add(Symbol symbol) {
        symbolMap.put(symbol.getName(), symbol);
    }

    public Symbol get(String name) {
        return symbolMap.get(name);
    }

    public boolean isFunc() {
        return type != null;
    }

    public boolean isIntFunc() {
        return type == FuncParam.Type.INT;
    }

    public boolean isVoidFunc() {
        return type == FuncParam.Type.VOID;
    }
}
