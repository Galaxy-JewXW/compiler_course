package error.symbol;

import java.util.HashMap;

public class SymbolTables {
    private final HashMap<String, Symbol> symbolMap;
    private Type type;

    public SymbolTables(Type type) {
        this.symbolMap = new HashMap<>();
        this.type = type;
    }

    public boolean isFunc() {
        return type != null;
    }

    public boolean isIntFunc() {
        return type == Type.INT;
    }

    public boolean isVoidFunc() {
        return type == Type.VOID;
    }

    public boolean contains(String name) {
        return symbolMap.containsKey(name);
    }

    public void addSymbol(Symbol symbol) {
        symbolMap.put(symbol.getName(), symbol);
    }

    public Symbol get(String name) {
        return symbolMap.get(name);
    }
}
