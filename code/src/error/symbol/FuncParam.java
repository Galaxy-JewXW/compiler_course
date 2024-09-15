package error.symbol;

public class FuncParam {
    private final String name;
    private final SymbolType type;
    private final int dimension;

    public FuncParam(String name, SymbolType type, int dimension) {
        this.name = name;
        this.type = type;
        this.dimension = dimension;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }

    public int getDimension() {
        return dimension;
    }
}
