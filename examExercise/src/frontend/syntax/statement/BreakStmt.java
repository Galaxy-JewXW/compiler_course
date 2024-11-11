package frontend.syntax.statement;

import frontend.token.Token;
import frontend.token.TokenType;

public class BreakStmt extends Stmt {
    private final Token token;

    public BreakStmt(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        System.out.println(token);
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<Stmt>");
    }
}
