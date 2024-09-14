package frontend;

public class Token {
    private final TokenType tokenType;
    private final String val;
    private final int line;

    public Token(TokenType tokenType, String val, int line) {
        this.tokenType = tokenType;
        this.val = val;
        this.line = line;
    }

    public TokenType getType() {
        return tokenType;
    }

    public String getValue() {
        return val;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return tokenType.name() + " " + val;
    }
}
