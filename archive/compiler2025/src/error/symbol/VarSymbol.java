package error.symbol;

public class VarSymbol extends Symbol {
    private final boolean isConstant;
    private final int dimension;

    public VarSymbol(String name, Type type, boolean isConstant, int dimension) {
        super(name, type);
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
