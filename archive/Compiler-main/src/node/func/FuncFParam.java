package node.func;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.BType;
import node.expression.ConstExp;
import error.symbol.Type;
import error.symbol.VarSymbol;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class FuncFParam {
    //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    private BType bType;
    private Token Ident;
    private ArrayList<ConstExp> constExps;
    private boolean isArray;

    public FuncFParam(BType bType, Token Ident,
                      ArrayList<ConstExp> constExps, boolean isArray) {
        this.bType = bType;
        this.Ident = Ident;
        this.constExps = constExps;
        this.isArray = isArray;
    }

    public Token getIdent() {
        return Ident;
    }

    public boolean isArray() {
        return isArray;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public int getDimension() {
        if (!isArray) {
            return 0;
        }
        return constExps.size() + 1;
    }

    public void print() {
        bType.print();
        OutputHandler.printToken(Ident);
        if (isArray) {
            OutputHandler.printToken(TokenType.LBRACK);
            OutputHandler.printToken(TokenType.RBRACK);
            for (ConstExp constExp : constExps) {
                OutputHandler.printToken(TokenType.LBRACK);
                constExp.print();
                OutputHandler.printToken(TokenType.RBRACK);
            }
        }
        OutputHandler.println("<FuncFParam>");
    }

    public void checkError() {
        if (ErrorHandler.getInstance().isInCurTable(Ident.getToken())) {
            ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.b, Ident.getLine()));
            return;
        }
        ErrorHandler.getInstance().addSymbol(
                new VarSymbol(Ident.getToken(), Type.INT, false, getDimension()));
    }
}
