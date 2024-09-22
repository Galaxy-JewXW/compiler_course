package frontend;

import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Symbol> symbols = new HashMap<>();
    private final SymbolType blockType;
    private final SymbolTable parent;
    private final ArrayList<SymbolTable> children = new ArrayList<>();

    public SymbolTable(SymbolType blockType, SymbolTable parent) {
        this.blockType = blockType;
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void addChild(SymbolTable child) {
        children.add(child);
    }

    public boolean isFunc() {
        return blockType != null;
    }

    public boolean isInt32Func() {
        return blockType == SymbolType.INT32;
    }

    public boolean isInt8Func() {
        return blockType == SymbolType.INT8;
    }

    public boolean isVoidFunc() {
        return blockType == SymbolType.VOID;
    }

    public boolean containsSymbol(String symbolName) {
        return symbols.containsKey(symbolName);
    }

    public Symbol getSymbol(String symbolName) {
        return symbols.get(symbolName);
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }
}
