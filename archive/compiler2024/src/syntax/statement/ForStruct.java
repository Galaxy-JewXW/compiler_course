package syntax.statement;

import error.ErrorVisitor;
import frontend.TokenType;
import syntax.expression.Cond;

public class ForStruct implements Stmt {
    private final ForStmt forStmt1;
    private final Cond cond;
    private final ForStmt forStmt2;
    private final Stmt stmt;

    public ForStruct(ForStmt forStmt1, Cond cond, ForStmt forStmt2, Stmt stmt) {
        this.forStmt1 = forStmt1;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.stmt = stmt;
    }

    public ForStmt getForStmt1() {
        return forStmt1;
    }

    public Cond getCond() {
        return cond;
    }

    public ForStmt getForStmt2() {
        return forStmt2;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public void output() {
        System.out.println(TokenType.FORTK.name() + " " + TokenType.FORTK);
        System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
        if (forStmt1 != null) {
            forStmt1.output();
        }
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        if (cond != null) {
            cond.output();
        }
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        if (forStmt2 != null) {
            forStmt2.output();
        }
        System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        stmt.output();
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        if (forStmt1 != null) {
            forStmt1.check();
        }
        if (cond != null) {
            cond.check();
        }
        if (forStmt2 != null) {
            forStmt2.check();
        }
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        errorVisitor.enterLoop();
        stmt.check();
        errorVisitor.exitLoop();
    }
}
