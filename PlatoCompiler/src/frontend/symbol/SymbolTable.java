package frontend.symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;
    private final ArrayList<SymbolTable> children = new ArrayList<>();

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public ArrayList<SymbolTable> getChildren() {
        return children;
    }

    public void addChild(SymbolTable child) {
        children.add(child);
    }

    public boolean contains(String name) {
        return symbols.containsKey(name);
    }

    public Symbol get(String name) {
        return symbols.get(name);
    }

    public void put(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }
}
