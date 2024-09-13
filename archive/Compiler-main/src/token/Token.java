package token;

public class Token {
    private final TokenType symbol;
    private final String token;

    private final int line;

    public Token(TokenType symbol, String token, int line) {
        this.token = token;
        this.symbol = symbol;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public String getToken() {
        return token;
    }

    public TokenType getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol.toString() + ' ' + token;
    }
}
