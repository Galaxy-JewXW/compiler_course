package frontend;

import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SymbolTable {
    private final LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();
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
        return blockType == SymbolType.INT;
    }

    public boolean isInt8Func() {
        return blockType == SymbolType.CHAR;
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

    public void print() {
        System.out.println("----------------");
        for (Symbol symbol : symbols.values()) {
            System.out.println(symbol);
        }
        System.out.println("----------------");
        for (SymbolTable child : children) {
            child.print();
        }
    }
}
