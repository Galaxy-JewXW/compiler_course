package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import node.Cond;
import node.ForStmt;
import token.TokenType;

public class For implements Stmt {
    //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    private final ForStmt forStmt1;
    private final Cond cond;
    private final ForStmt forStmt2;
    private final Stmt stmt;

    public For(ForStmt forStmt1, Cond cond, ForStmt forStmt2, Stmt stmt) {
        this.forStmt1 = forStmt1;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.stmt = stmt;
    }

    public Cond getCond() {
        return cond;
    }

    public ForStmt getForStmt1() {
        return forStmt1;
    }

    public ForStmt getForStmt2() {
        return forStmt2;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.FORTK);
        OutputHandler.printToken(TokenType.LPARENT);
        if (forStmt1 != null) {
            forStmt1.print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        if (cond != null) {
            cond.print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        if (forStmt2 != null) {
            forStmt2.print();
        }
        OutputHandler.printToken(TokenType.RPARENT);
        stmt.print();
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        if (forStmt1 != null) {
            forStmt1.checkError();
        }
        if (cond != null) {
            cond.checkError();
        }
        if (forStmt2 != null) {
            forStmt2.checkError();
        }
        ErrorHandler.getInstance().addLoopLev();
        stmt.checkError();
        ErrorHandler.getInstance().minusLoopLev();
    }
}
