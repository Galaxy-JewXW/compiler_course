package frontend.symbol;

import middle.component.InitialValue;
import middle.component.model.Value;

public class ConstSymbol extends Symbol {
    private VarType varType;
    private int dimension;
    private int length;
    private InitialValue initialValue;
    private boolean isGlobal;
    private Value value = null;

    public ConstSymbol(String name, VarType varType,
                     int dimension, int length) {
        super(name, SymbolType.CONSTANT);
        this.varType = varType;
        this.dimension = dimension;
        this.length = length;
        this.initialValue = null;
        this.isGlobal = TableManager.getInstance().isInGlobal();
    }

    public ConstSymbol(String name, VarType varType, int dimension,
                     int length, InitialValue initialValue) {
        this(name, varType, dimension, length);
        this.initialValue = initialValue;
    }

    public int getLength() {
        return length;
    }

    public VarType getVarType() {
        return varType;
    }

    public int getDimension() {
        return dimension;
    }

    public InitialValue getInitialValue() {
        return initialValue;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public int getConst() {
        return initialValue.getElements().get(0);
    }

    public int getConst(int index) {
        return initialValue.getElements().get(index);
    }
}
