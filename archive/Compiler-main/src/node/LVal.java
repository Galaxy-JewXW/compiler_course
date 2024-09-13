package node;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.expression.Exp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class LVal {
    //LVal → Ident {'[' Exp ']'}
    private Token Ident;
    private ArrayList<Exp> exps;

    public LVal(Token Ident, ArrayList<Exp> exps) {
        this.Ident = Ident;
        this.exps = exps;
    }

    public Token getIdent() {
        return this.Ident;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public void print() {
        OutputHandler.printToken(Ident);
        for (Exp exp : exps) {
            OutputHandler.printToken(TokenType.LBRACK);
            exp.print();
            OutputHandler.printToken(TokenType.RBRACK);
        }
        OutputHandler.println("<LVal>");
    }

    public void checkError() {
        if (!ErrorHandler.getInstance().defined(Ident.getToken())) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.c, Ident.getLine()));
        }
        for (Exp exp : exps) {
            exp.checkError();
        }
    }
}
