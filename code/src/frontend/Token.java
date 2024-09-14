package frontend;

/**
 * 词法单元类，记录了类型，内容（字符串形式）和所在行号
 */
public class Token {
    private final TokenType tokenType;
    private final String content;
    private final int line; // 主要用于异常处理的信息输出

    public Token(TokenType tokenType, String content, int line) {
        this.tokenType = tokenType;
        this.content = content;
        this.line = line;
    }

    public TokenType getType() {
        return tokenType;
    }

    public String getContent() {
        return content;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return tokenType.name() + " " + content;
    }
}
