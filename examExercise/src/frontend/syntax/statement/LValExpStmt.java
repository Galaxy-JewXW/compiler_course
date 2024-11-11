package frontend.syntax.statement;

import frontend.syntax.LVal;
import frontend.syntax.expression.Exp;
import frontend.token.TokenType;

public class LValExpStmt extends Stmt {
    private final LVal lval;
    private final Exp exp;

    public LValExpStmt(LVal lval, Exp exp) {
        this.lval = lval;
        this.exp = exp;
    }

    public LVal getLVal() {
        return lval;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        lval.print();
        System.out.println(TokenType.printType(TokenType.ASSIGN));
        exp.print();
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<Stmt>");
    }
}
