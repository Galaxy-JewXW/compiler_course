package frontend.syntax.statement;

import frontend.TokenType;
import frontend.syntax.expression.Exp;

public class ReturnStmt extends Stmt {
    private final Exp exp;

    public ReturnStmt(Exp exp) {
        this.exp = exp;
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
