package middle;

import middle.component.model.Value;

import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private final Stack<HashMap<String, Value>> symbols = new Stack<>();
    private final Stack<HashMap<String, Integer>> consts = new Stack<>();

    public void addTable() {
        symbols.push(new HashMap<>());
        consts.push(new HashMap<>());
    }

    public void removeTable() {
        symbols.pop();
        consts.pop();
    }

    public void addSymbol(String name, Value value) {
        symbols.peek().put(name, value);
    }

    public void addConst(String name, int value) {
        consts.peek().put(name, value);
    }

    public void addGlobalSymbol(String name, Value value) {
        if (!symbols.isEmpty()) {
            symbols.firstElement().put(name, value);
        }
    }

    public Value getValue(String name) {
        for (int i = symbols.size() - 1; i >= 0; i--) {
            Value value = symbols.get(i).get(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public int getConst(String name) {
        for (int i = consts.size() - 1; i >= 0; i--) {
            Integer value = consts.get(i).get(name);
            if (value != null) {
                return value;
            }
        }
        throw new RuntimeException();
    }
}
