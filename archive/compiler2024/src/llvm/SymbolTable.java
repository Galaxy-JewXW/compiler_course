package llvm;

import llvm.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final ArrayList<HashMap<String, Value>> valuesTable = new ArrayList<>();
    private final ArrayList<HashMap<String, Integer>> constTable = new ArrayList<>();

    public void addTable() {
        valuesTable.add(new HashMap<>());
        constTable.add(new HashMap<>());
    }

    public void removeTable() {
        valuesTable.remove(valuesTable.size() - 1);
        constTable.remove(constTable.size() - 1);
    }

    public void addSymbol(String name, Value value) {
        valuesTable.get(valuesTable.size() - 1).put(name, value);
    }

    public void addConst(String name, int value) {
        constTable.get(constTable.size() - 1).put(name, value);
    }

    public void addGlobalSymbol(String name, Value value) {
        valuesTable.get(0).put(name, value);
    }

    public Value getValue(String name) {
        for (int i = valuesTable.size() - 1; i >= 0; i--) {
            if (valuesTable.get(i).containsKey(name)) {
                return valuesTable.get(i).get(name);
            }
        }
        return null;
    }

    public int getConst(String name) {
        for (int i = constTable.size() - 1; i >= 0; i--) {
            if (constTable.get(i).containsKey(name)) {
                return constTable.get(i).get(name);
            }
        }
        return 0;
    }
}
