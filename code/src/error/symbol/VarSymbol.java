package error.symbol;

public class VarSymbol extends Symbol {
    private final boolean isConstant;
    private final int dimension;

    public VarSymbol(String name, SymbolType symbolType, boolean constant, int dimension) {
        super(name, symbolType);
        this.isConstant = constant;
        this.dimension = dimension;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public int getDimension() {
        return dimension;
    }
}
