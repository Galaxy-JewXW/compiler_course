package backend.Symbol;

import IR.values.NullValue;
import IR.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsSymbolTable {
    private final ArrayList<HashMap<String, MipsSymbol>> symbolTables = new ArrayList<>();

    public void addSymbolTable() {
        this.symbolTables.add(new HashMap<>());
    }

    public void rmSymbolTable() {
        this.symbolTables.remove(symbolTables.size() - 1);
    }

    public void addSymbol(String name, MipsSymbol mipsSymbol) {
        this.symbolTables.get(symbolTables.size() - 1).put(name, mipsSymbol);
    }

    public MipsSymbol getSymbol(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).containsKey(name)) {
                return symbolTables.get(i).get(name);
            }
        }
        return null;
    }

    public boolean contains(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }
}

