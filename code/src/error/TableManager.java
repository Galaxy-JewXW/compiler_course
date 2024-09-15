package error;

import error.symbol.Symbol;
import error.symbol.SymbolType;

public class TableManager {
    public static final TableManager INSTANCE = new TableManager();

    private TableManager() {}

    public static TableManager getInstance() {
        return INSTANCE;
    }

    // 初始化为CompUnit对应的最高级的符号表
    private SymbolTable currentTable = new SymbolTable(null, null);;

    public void addTable(SymbolType blockType) {
        currentTable = new SymbolTable(blockType, currentTable);
    }

    public boolean inCurrentTable(String symbolName) {
        return currentTable.containsSymbol(symbolName);
    }

    public void addSymbol(Symbol symbol) {
        currentTable.addSymbol(symbol);
    }
}
