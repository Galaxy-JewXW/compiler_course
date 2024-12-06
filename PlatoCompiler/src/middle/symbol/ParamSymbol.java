package middle.symbol;

import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

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

    public ValueType getValueType() {
        ValueType basicType = switch (type) {
            case INT -> IntegerType.i32;
            case CHAR -> IntegerType.i8;
            case VOID -> throw new RuntimeException();
        };
        if (dimension == 1) {
            basicType = new PointerType(basicType);
        }
        return basicType;
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
