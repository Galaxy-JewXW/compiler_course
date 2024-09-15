package frontend;

import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;
import frontend.symbol.VarSymbol;

public class TableManager {
    // 单例模式
    private static final TableManager INSTANCE = new TableManager();

    public static TableManager getInstance() {
        return INSTANCE;
    }

    private TableManager() {
    }

    private final SymbolTable rootTable = new SymbolTable(null, null);
    // 初始化为CompUnit对应的最高级的符号表
    private SymbolTable currentTable = rootTable;
    // 当前处于的循环层数
    private int loopLevel = 0;

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

    public boolean inVoidFunc() {
        return currentTable.isVoidFunc();
    }

    /**
     * 在符号表树中查找标识符所对应的symbol
     * 如果在本级符号表中无法查询到该symbol，则在其父节点上查找
     */
    public Symbol getSymbol(String symbolName) {
        SymbolTable table = currentTable;
        while (table != null) {
            if (table.containsSymbol(symbolName)) {
                return table.getSymbol(symbolName);
            }
            table = table.getParent();
        }
        return null;
    }

    public boolean isConstantVarSymbol(String symbolName) {
        Symbol symbol = getSymbol(symbolName);
        if (symbol instanceof VarSymbol varSymbol) {
            return varSymbol.isConstant();
        } else {
            return false;
        }
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
}
