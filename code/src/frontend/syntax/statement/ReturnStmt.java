package frontend.syntax.statement;

import frontend.syntax.expression.Exp;
import frontend.token.Token;
import frontend.token.TokenType;

public class ReturnStmt extends Stmt {
    private final Token token;
    private final Exp exp;

    public ReturnStmt(Token token, Exp exp) {
        this.token = token;
        this.exp = exp;
    }

    public Token getToken() {
        return token;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.RETURNTK));
        if (exp != null) {
            exp.print();
        }
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<Stmt>");
    }
}
