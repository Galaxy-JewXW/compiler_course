package llvm;

import llvm.values.Value;
import java.util.HashMap;
import java.util.Stack;

public class SymbolTableManager {
    private final Stack<HashMap<String, Value>> symbolTables = new Stack<>();
    private final Stack<HashMap<String, Integer>> constTables = new Stack<>();

    public void addTable() {
        symbolTables.push(new HashMap<>());
        constTables.push(new HashMap<>());
    }

    public void removeTable() {
        symbolTables.pop();
        constTables.pop();
    }

    public void addSymbol(String name, Value value) {
        symbolTables.peek().put(name, value);
    }

    public void addConst(String name, int value) {
        constTables.peek().put(name, value);
    }

    public void addGlobalSymbol(String name, Value value) {
        if (!symbolTables.isEmpty()) {
            symbolTables.firstElement().put(name, value);
        }
    }

    public Value getValue(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            Value value = symbolTables.get(i).get(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public int getConst(String name) {
        for (int i = constTables.size() - 1; i >= 0; i--) {
            Integer value = constTables.get(i).get(name);
            if (value != null) {
                return value;
            }
        }
        return 0;
    }
}
