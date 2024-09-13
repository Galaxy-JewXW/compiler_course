package node;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.expression.ConstExp;
import node.expression.Exp;
import token.TokenType;

public class ForStmt {
    //ForStmt → LVal '=' Exp
    private LVal lVal;
    private Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public void print() {
        lVal.print();
        OutputHandler.printToken(TokenType.ASSIGN);
        exp.print();
        OutputHandler.println("<ForStmt>");
    }

    public void checkError() {
        lVal.checkError();
        if (ErrorHandler.getInstance().isCon(lVal.getIdent().getToken())) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.h, lVal.getIdent().getLine()));
        }
        exp.checkError();
    }
}
