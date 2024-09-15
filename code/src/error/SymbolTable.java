package error;

import error.symbol.Symbol;
import error.symbol.SymbolType;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Symbol> symbolTable = new HashMap<>();
    private final SymbolType blockType;
    private final SymbolTable parent;

    public SymbolTable(SymbolType blockType, SymbolTable parent) {
        this.blockType = blockType;
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
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
        return symbolTable.containsKey(symbolName);
    }

    public Symbol getSymbol(String symbolName) {
        return symbolTable.get(symbolName);
    }

    public void addSymbol(Symbol symbol) {
        symbolTable.put(symbol.getName(), symbol);
    }
}
