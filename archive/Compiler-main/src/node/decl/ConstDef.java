package node.decl;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.expression.ConstExp;
import error.symbol.Type;
import error.symbol.VarSymbol;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class ConstDef {
    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
    private Token Ident;
    private ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;

    public ConstDef(Token Ident, ArrayList<ConstExp> constExps,
                    ConstInitVal constInitVal) {
        this.Ident = Ident;
        this.constInitVal = constInitVal;
        this.constExps = constExps;
    }

    public void print() {
        OutputHandler.printToken(Ident);
        for (ConstExp constExp : constExps) {
            OutputHandler.printToken(TokenType.LBRACK);
            constExp.print();
            OutputHandler.printToken(TokenType.RBRACK);
        }
        OutputHandler.printToken(TokenType.ASSIGN);
        constInitVal.print();
        OutputHandler.println("<ConstDef>");
    }

    public void checkError() {
        if (ErrorHandler.getInstance().isInCurTable(Ident.getToken())) {
            ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.b, Ident.getLine()));
            return;
        }
        for (ConstExp constExp : constExps) {
            constExp.checkError();
        }
        //未来可能有类型的迭代，可以通过checkError来传递类型参数
        ErrorHandler.getInstance().addSymbol(
                new VarSymbol(Ident.getToken(), Type.INT, true, constExps.size()));
        constInitVal.checkError();
    }

    public Token getIdent() {
        return Ident;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }
}
