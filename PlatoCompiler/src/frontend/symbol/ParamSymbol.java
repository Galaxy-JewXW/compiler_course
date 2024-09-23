package frontend.symbol;

/**
 * 函数形参符号类，记录函数形参的名称，类型和维数
 */
public class ParamSymbol {
    private final String name;
    private final SymbolType type;
    private final int dimension;

    public ParamSymbol(String name, SymbolType type, int dimension) {
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

    @Override
    public String toString() {
        return "ParamSymbol{" +
                "name='" + name + '\'' +
                ", type=" + type.name() +
                ", dimension=" + dimension +
                '}';
    }
}
