package error.symbol;

public class FuncParam {
    private String name;
    private Type type;
    private int dimension;

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
