package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import token.Token;
import token.TokenType;

public class ContinueStmt implements Stmt {
    //'continue' ';'
    private Token continueToken;

    public ContinueStmt(Token continueToken) {
        this.continueToken = continueToken;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.CONTINUETK);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        if (!ErrorHandler.getInstance().isInLoop()) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.m, continueToken.getLine()));
        }
    }
}
