package middle.symbol;

import middle.component.InitialValue;
import middle.component.model.Value;

public class VarSymbol extends Symbol {
    private static int counter = 0;
    private final int id;
    private final boolean isConstant;
    private final int dimension;
    // 为数组时所定义的长度：a[6] -> length = 6
    private final int length;
    private final InitialValue initialValue;
    private Value llvmValue = null;

    public VarSymbol(String name, SymbolType symbolType, boolean isConstant,
                     int dimension, int length, InitialValue initialValue) {
        super(name, symbolType);
        this.id = counter++;
        this.isConstant = isConstant;
        this.dimension = dimension;
        this.length = length;
        this.initialValue = initialValue;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public int getDimension() {
        return dimension;
    }

    public int getConstValue() {
        return initialValue.getElements().get(0);
    }

    public int getConstValue(int index) {
        return initialValue.getElements().get(index);
    }

    public InitialValue getInitialValue() {
        return initialValue;
    }

    public Value getLlvmValue() {
        return llvmValue;
    }

    public void setLlvmValue(Value llvmValue) {
        this.llvmValue = llvmValue;
    }

    @Override
    public String toString() {
        return "VarSymbol_" + id + " {" +
                "name='" + getName() + '\'' +
                ", type=" + getType() +
                ", isConstant=" + isConstant +
                ", dimension=" + dimension +
                ", length=" + length +
                ", initialValue=" + initialValue +
                ", llvmValue=" + llvmValue +
                '}';
    }
}
