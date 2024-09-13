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

public class VarDef {
    //VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
    //  | Ident { '[' ConstExp ']' } '=' InitVal
    private Token Ident;
    private ArrayList<ConstExp> constExps;
    private InitVal initVal;

    public VarDef(Token Ident, ArrayList<ConstExp> constExps, InitVal initVal) {
        this.Ident = Ident;
        this.constExps = constExps;
        this.initVal = initVal;
    }

    public Token getIdent() {
        return Ident;
    }

    public InitVal getInitVal() {
        return initVal;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public void print() {
        OutputHandler.printToken(Ident);
        for (ConstExp constExp : constExps) {
            OutputHandler.printToken(TokenType.LBRACK);
            constExp.print();
            OutputHandler.printToken(TokenType.RBRACK);
        }
        if (initVal != null) {
            OutputHandler.printToken(TokenType.ASSIGN);
            initVal.print();
        }
        OutputHandler.println("<VarDef>");
    }

    public void checkError() {
        if (ErrorHandler.getInstance().isInCurTable(Ident.getToken())) {
            ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.b, Ident.getLine()));
            return;
        }
        for (ConstExp constExp : constExps) {
            constExp.checkError();
        }
        ErrorHandler.getInstance().addSymbol(
                new VarSymbol(Ident.getToken(), Type.INT, false, constExps.size()));
        if (initVal != null) {
            initVal.checkError();
        }
    }
}
