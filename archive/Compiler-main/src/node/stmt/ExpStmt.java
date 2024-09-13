package node.stmt;

import IO.OutputHandler;
import node.expression.Exp;
import token.TokenType;

public class ExpStmt implements Stmt {
    //[Exp] ';'
    private Exp exp;

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
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        if (exp != null) {
            exp.checkError();
        }
    }
}
