package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import token.Token;
import token.TokenType;

public class BreakStmt implements Stmt {
    //'break' ';'
    private Token breakToken;

    public BreakStmt(Token breakToken) {
        this.breakToken = breakToken;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.BREAKTK);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        if (!ErrorHandler.getInstance().isInLoop()) {
            ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.m, breakToken.getLine()));
        }
    }

}
