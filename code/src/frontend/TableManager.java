package frontend;

import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;

public class TableManager {
    // 初始化为CompUnit对应的最高级的符号表
    private SymbolTable currentTable = new SymbolTable(null, null);

    public void addTable(SymbolType blockType) {
        currentTable = new SymbolTable(blockType, currentTable);
    }

    public void popTable() {
        currentTable = currentTable.getParent();
    }

    public boolean inCurrentTable(String symbolName) {
        return currentTable.containsSymbol(symbolName);
    }

    public void addSymbol(Symbol symbol) {
        currentTable.addSymbol(symbol);
    }

    public boolean inReturnValueFunc() {
        return currentTable.isInt8Func() || currentTable.isInt32Func();
    }
}
