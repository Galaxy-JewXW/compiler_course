package frontend;

import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;
import frontend.symbol.VarSymbol;

import java.util.HashSet;

public class TableManager {
    // 单例模式
    private static final TableManager INSTANCE = new TableManager();
    // 初始化为CompUnit对应的最高级的符号表
    private SymbolTable currentTable = new SymbolTable(null, null);
    // 当前处于的循环层数
    private int loopLevel = 0;
    private HashSet<VarSymbol> localConstArray = new HashSet<>();

    public TableManager() {
    }

    public static TableManager getInstance() {
        return INSTANCE;
    }

    public void createTable(SymbolType blockType) {
        /*
         * BlockType记录该Block对应的返回值类型
         * 如果一个Block直接位于MainFuncDef或FuncDef之中，其blockType属性设置为函数定义的返回值
         * 否则设置为null
         */
        SymbolTable newTable = new SymbolTable(blockType, currentTable);
        currentTable.addChild(newTable);
        currentTable = newTable;
    }

    public void recoverTable() {
        currentTable = currentTable.getParent();
    }

    public boolean inCurrentTable(String symbolName) {
        return currentTable.containsSymbol(symbolName);
    }

    public void addSymbol(Symbol symbol) {
        currentTable.addSymbol(symbol);
    }

    public void addLocalConstArray(VarSymbol var) {
        localConstArray.add(var);
    }

    public HashSet<VarSymbol> getLocalConstArray() {
        return localConstArray;
    }

    public boolean isFuncFirst() {
        return currentTable.isFuncFirst();
    }

    /*
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

    public SymbolTable getCurrentTable() {
        return currentTable;
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

    public void print() {
        currentTable.print();
    }

    public void show() {
        currentTable.show();
    }
}
