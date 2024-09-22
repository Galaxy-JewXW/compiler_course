package frontend.symbol;

import middle.component.InitialValue;
import middle.component.model.Value;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private VarType varType;
    private int dimension;
    private ArrayList<Integer> indexes;
    private InitialValue initialValue;
    private boolean isGlobal;
    private Value value = null;

    public VarSymbol(String name, VarType varType,
                     int dimension, ArrayList<Integer> indexes) {
        super(name, SymbolType.VARIABLE);
        this.varType = varType;
        this.dimension = dimension;
        this.indexes = indexes;
        this.initialValue = null;
        this.isGlobal = TableManager.getInstance().isGlobal();
    }

    public VarSymbol(String name, VarType varType, int dimension,
                     ArrayList<Integer> indexes, InitialValue initialValue) {
        this(name, varType, dimension, indexes);
        this.initialValue = initialValue;
    }

    public int getLength() {
        return indexes.get(0);
    }

    public VarType getVarType() {
        return varType;
    }

    public int getDimension() {
        return dimension;
    }

    public ArrayList<Integer> getIndexes() {
        return indexes;
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
