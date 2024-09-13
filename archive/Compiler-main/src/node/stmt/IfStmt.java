package node.stmt;

import IO.OutputHandler;
import node.Cond;
import token.TokenType;

public class IfStmt implements Stmt {
    //'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    private Cond cond;
    private Stmt stmt1;
    private Stmt stmt2;

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
    public void print() {
        OutputHandler.printToken(TokenType.IFTK);
        OutputHandler.printToken(TokenType.LPARENT);
        cond.print();
        OutputHandler.printToken(TokenType.RPARENT);
        stmt1.print();
        if (stmt2 != null) {
            OutputHandler.printToken(TokenType.ELSETK);
            stmt2.print();
        }
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        cond.checkError();
        stmt1.checkError();
        if (stmt2 != null) {
            stmt2.checkError();
        }
    }
}
