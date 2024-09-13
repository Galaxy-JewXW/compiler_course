package error.symbol;

public class Symbol {
    private final String name;
    private final Type type;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
