package frontend.syntax.statement;

import frontend.syntax.expression.Exp;
import frontend.token.TokenType;

public class ExpStmt extends Stmt {
    private final Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        if (exp != null) {
            exp.print();
        }
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<Stmt>");
    }
}
