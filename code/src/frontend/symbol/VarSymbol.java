package frontend.symbol;

public class VarSymbol extends Symbol {
    private final boolean isConstant;
    private final int dimension;

    public VarSymbol(String name, SymbolType symbolType, boolean isConstant, int dimension) {
        super(name, symbolType);
        this.isConstant = isConstant;
        this.dimension = dimension;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public int getDimension() {
        return dimension;
    }
}
