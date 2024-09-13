package error.symbol;

public class FuncParam {
    private final String name;
    private final Type type;
    private final int dimension;

    public FuncParam(String name, Type type, int dimension) {
        this.name = name;
        this.type = type;
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public String getName() {
        return name;
    }
}
