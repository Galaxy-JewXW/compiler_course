package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.LVal;
import token.TokenType;

public class AssignGetintStmt implements Stmt {
    //LVal '=' 'getint''('')'';'
    private LVal lVal;

    public AssignGetintStmt(LVal lVal) {
        this.lVal = lVal;
    }

    public LVal getlVal() {
        return lVal;
    }

    @Override
    public void print() {
        lVal.print();
        OutputHandler.printToken(TokenType.ASSIGN);
        OutputHandler.printToken(TokenType.GETINTTK);
        OutputHandler.printToken(TokenType.LPARENT);
        OutputHandler.printToken(TokenType.RPARENT);
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
    }
}
