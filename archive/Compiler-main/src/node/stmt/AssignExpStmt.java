package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.expression.Exp;
import node.LVal;
import token.TokenType;

public class AssignExpStmt implements Stmt {
    //LVal '=' Exp ';'
    private LVal lVal;
    private Exp exp;

    public AssignExpStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        lVal.print();
        OutputHandler.printToken(TokenType.ASSIGN);
        exp.print();
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        lVal.checkError();
        if (ErrorHandler.getInstance().isCon(lVal.getIdent().getToken())) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.h, lVal.getIdent().getLine()));
        }
        exp.checkError();
    }

}
