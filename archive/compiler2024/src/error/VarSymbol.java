package error;

public class VarSymbol implements Symbol {
    private final String name;
    private final FuncParam.Type type;
    private final boolean isConstant;
    private final int dimension;

    public VarSymbol(String name, FuncParam.Type type, boolean isConstant, int dimension) {
        this.name = name;
        this.type = type;
        this.isConstant = isConstant;
        this.dimension = dimension;
    }

    @Override
    public FuncParam.Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public int getDimension() {
        return dimension;
    }
}
