package node.stmt;

import IO.OutputHandler;

import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.expression.Exp;
import token.Token;
import token.TokenType;

public class ReturnStmt implements Stmt {
    //'return' [Exp] ';'
    private Token returnToken;
    private Exp exp;

    public ReturnStmt(Token returnToken, Exp exp) {
        this.returnToken = returnToken;
        this.exp = exp;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.RETURNTK);
        if (exp != null) {
            exp.print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        if (ErrorHandler.getInstance().inVoidFunc() && exp != null) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.f, returnToken.getLine()));
        }
        if (exp != null) {
            exp.checkError();
        }
    }

    public Exp getExp() {
        return exp;
    }
}
