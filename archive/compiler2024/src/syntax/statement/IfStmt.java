package syntax.statement;

import frontend.TokenType;
import syntax.expression.Cond;

public class IfStmt implements Stmt {
    private final Cond cond;
    private final Stmt stmt1; // if statement
    private final Stmt stmt2; // else statement

    public IfStmt(Cond cond, Stmt stmt1, Stmt stmt2) {
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
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
    public void output() {
        System.out.println(TokenType.IFTK.name() + " " + TokenType.IFTK);
        System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
        cond.output();
        System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        stmt1.output();
        if (stmt2 != null) {
            System.out.println(TokenType.ELSETK.name() + " " + TokenType.ELSETK);
            stmt2.output();
        }
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        cond.check();
        stmt1.check();
        if (stmt2 != null) {
            stmt2.check();
        }
    }

}
