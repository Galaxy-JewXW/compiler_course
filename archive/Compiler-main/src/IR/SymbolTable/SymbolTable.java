package IR.SymbolTable;

import IR.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final ArrayList<HashMap<String, Value>> symbolTables;
    private final ArrayList<HashMap<String, Integer>> constTables;

    public SymbolTable() {
        this.symbolTables = new ArrayList<>();
        this.constTables = new ArrayList<>();
    }

    public void addSymbolTable() {
        this.symbolTables.add(new HashMap<>());
        this.constTables.add(new HashMap<>());
    }

    public void rmSymbolTable() {
        this.symbolTables.remove(symbolTables.size() - 1);
        this.constTables.remove(symbolTables.size() - 1);
    }

    public void addSymbol(String name, Value value) {
        this.symbolTables.get(symbolTables.size() - 1).put(name, value);
    }

    public void addConst(String name, int value) {
        this.constTables.get(constTables.size() - 1).put(name, value);
    }

    public void addGlobalSymbol(String name, Value value) {
        this.symbolTables.get(0).put(name, value);
    }

    public Value getValue(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).containsKey(name)) {
                return symbolTables.get(i).get(name);
            }
        }
        return null;
    }

    public int getConst(String name) {
        for (int i = constTables.size() - 1; i >= 0; i--) {
            if (constTables.get(i).containsKey(name)) {
                return constTables.get(i).get(name);
            }
        }
        return 0;
    }
}
