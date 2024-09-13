package syntax.statement;

import frontend.TokenType;
import syntax.expression.Exp;

public class ExpStmt implements Stmt {
    private final Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output() {
        if (exp != null) {
            exp.output();
        }
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        if (exp != null) {
            exp.check();
        }
    }
}
