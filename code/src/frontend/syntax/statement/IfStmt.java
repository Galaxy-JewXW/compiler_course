package frontend.syntax.statement;

import frontend.syntax.expression.Cond;
import frontend.token.TokenType;

public class IfStmt extends Stmt {
    private final Cond cond;
    private final Stmt stmt1; // if-then statement
    private final Stmt stmt2; // else statement

    public IfStmt(Cond cond, Stmt stmt1, Stmt stmt2) {
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
    }

    public IfStmt(Cond cond, Stmt stmt1) {
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = null;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt1() {
        return stmt1;
    }

    public Stmt getStmt2() {
        return stmt2;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.IFTK));
        System.out.println(TokenType.printType(TokenType.LPARENT));
        cond.print();
        System.out.println(TokenType.printType(TokenType.RPARENT));
        stmt1.print();
        if (stmt2 != null) {
            System.out.println(TokenType.printType(TokenType.ELSETK));
            stmt2.print();
        }
        System.out.println("<Stmt>");
    }
}
