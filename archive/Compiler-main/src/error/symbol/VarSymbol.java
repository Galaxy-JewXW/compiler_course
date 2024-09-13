package error.symbol;

public class VarSymbol extends Symbol {
    private boolean isCon;
    private int dimension;

    public VarSymbol(String name, Type type, boolean isCon, int dimension) {
        super(name, type);
        this.isCon = isCon;
        this.dimension = dimension;
    }

    public boolean isCon() {
        return isCon;
    }

    public int getDimension() {
        return dimension;
    }
}
