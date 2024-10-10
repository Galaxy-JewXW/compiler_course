package frontend;

import frontend.symbol.FuncSymbol;
import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;
import frontend.symbol.VarSymbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    private static int counter = 0;
    private final LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();
    private final SymbolType blockType;
    private final SymbolTable parent;
    private final ArrayList<SymbolTable> children = new ArrayList<>();
    // 生成中间代码时在子表中查找
    private int childrenPointer = 0;

    public SymbolTable(SymbolType blockType, SymbolTable parent) {
        this.blockType = blockType;
        this.parent = parent;
    }

    public SymbolTable getChild() {
        return children.get(childrenPointer++);
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void addChild(SymbolTable child) {
        children.add(child);
    }

    public boolean isFuncFirst() {
        return blockType != null;
    }

    public boolean containsSymbol(String symbolName) {
        return symbols.containsKey(symbolName);
    }

    public Symbol getSymbol(String symbolName) {
        return symbols.get(symbolName);
    }

    // 从这个表开始逐级向上查找元素
    public Symbol findSymbol(String symbolName) {
        SymbolTable table = this;
        while (table != null) {
            if (table.containsSymbol(symbolName)) {
                return table.getSymbol(symbolName);
            }
            table = table.getParent();
        }
        return null;
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public void print() {
        System.out.println("----------------");
        for (Symbol symbol : symbols.values()) {
            System.out.println(symbol);
        }
        System.out.println("----------------");
        for (SymbolTable child : children) {
            child.print();
        }
    }

    public void show() {
        counter++;
        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            StringBuilder sb = new StringBuilder(Integer.toString(counter));
            sb.append(" ").append(entry.getKey()).append(" ");
            Symbol symbol = entry.getValue();
            String typeString = "";
            if (symbol instanceof FuncSymbol funcSymbol) {
                if (funcSymbol.getName().equals("main")) {
                    continue;
                }
                typeString = switch (funcSymbol.getType()) {
                    case VOID -> "VoidFunc";
                    case INT -> "IntFunc";
                    case CHAR -> "CharFunc";
                };
            } else if (symbol instanceof VarSymbol varSymbol) {
                if (varSymbol.isConstant()) {
                    if (varSymbol.getDimension() == 1) {
                        typeString = varSymbol.getType().equals(SymbolType.INT)
                                ? "ConstIntArray" : "ConstCharArray";
                    } else {
                        typeString = varSymbol.getType().equals(SymbolType.INT)
                                ? "ConstInt" : "ConstChar";
                    }
                } else {
                    if (varSymbol.getDimension() == 1) {
                        typeString = varSymbol.getType().equals(SymbolType.INT)
                                ? "IntArray" : "CharArray";
                    } else {
                        typeString = varSymbol.getType().equals(SymbolType.INT)
                                ? "Int" : "Char";
                    }
                }
            }
            sb.append(typeString);
            System.out.println(sb);
        }
        for (SymbolTable child : children) {
            child.show();
        }
    }
}
