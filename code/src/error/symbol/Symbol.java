package error.symbol;

public class Symbol {
    private final String name;
    private final SymbolType type;

    public Symbol(String name, SymbolType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }
}
