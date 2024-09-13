package syntax.expression;

import frontend.Token;

// 单目运算符 UnaryOp -> '+' | '-' | '!'
public class UnaryOp {
    private final Token token;

    public UnaryOp(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void output() {
        System.out.println(token);
        System.out.println("<UnaryOp>");
    }

}
