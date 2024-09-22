package frontend.symbol;

public class TableManager {
    private static final TableManager INSTANCE = new TableManager();
    private int loopLevel = 0;
    private boolean inGlobal = false;
    private SymbolTable currentTable = new SymbolTable(null);
    private FuncSymbol currentFunction = null;

    public static TableManager getInstance() {
        return INSTANCE;
    }

    private TableManager() {
    }

    public void createTable() {
        SymbolTable newTable = new SymbolTable(currentTable);
        currentTable.addChild(newTable);
        currentTable = newTable;
    }

    /*
     * 在符号表树中查找标识符所对应的symbol
     * 如果在本级符号表中无法查询到该symbol，则在其父节点上查找
     */
    public Symbol getSymbol(String symbolName) {
        SymbolTable table = currentTable;
        while (table != null) {
            if (table.contains(symbolName)) {
                return table.get(symbolName);
            }
            table = table.getParent();
        }
        return null;
    }

    public void removeTable() {
        currentTable = currentTable.getParent();
    }

    public boolean inCurrentTable(String name) {
        return currentTable.contains(name);
    }

    public void addSymbol(Symbol symbol) {
        currentTable.put(symbol.getName(), symbol);
    }

    public boolean isInGlobal() {
        return inGlobal;
    }

    public void setInGlobal(boolean isGlobal) {
        this.inGlobal = isGlobal;
    }

    // 维护循环层数loopLevel，进出循环体时更改计数
    public void enterLoop() {
        loopLevel++;
    }

    public void exitLoop() {
        loopLevel--;
    }

    public boolean notInLoop() {
        return loopLevel == 0;
    }

    public void enterFunction(FuncSymbol function) {
        currentFunction = function;
        createTable();
    }

    public void exitFunction() {
        currentFunction = null;
        removeTable();
    }
}
