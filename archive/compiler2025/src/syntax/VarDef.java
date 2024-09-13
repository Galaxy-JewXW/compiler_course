package syntax;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import error.symbol.Type;
import error.symbol.VarSymbol;
import frontend.Token;
import frontend.TokenType;
import syntax.expression.ConstExp;

import java.util.ArrayList;

// 变量定义 VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
public class VarDef {
    private final Token ident;
    private final ArrayList<ConstExp> constExps;
    private final InitVal initVal;

    public VarDef(Token ident, ArrayList<ConstExp> constExps, InitVal initVal) {
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = initVal;
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public InitVal getInitVal() {
        return initVal;
    }

    public void output() {
        System.out.println(ident);
        for (ConstExp constExp : constExps) {
            System.out.println(TokenType.LBRACK.name() + " " + TokenType.LBRACK);
            constExp.output();
            System.out.println(TokenType.RBRACK.name() + " " + TokenType.RBRACK);
        }
        if (initVal != null) {
            System.out.println(TokenType.ASSIGN.name() + " " + TokenType.ASSIGN);
            initVal.output();
        }
        System.out.println("<VarDef>");
    }

    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (errorVisitor.inCurrentTable(ident.getValue())) {
            errorVisitor.addError(new ErrorLog(ErrorType.IdentRedefined, ident.getLine()));
            return;
        }
        for (ConstExp constExp : constExps) {
            constExp.check();
        }
        errorVisitor.addSymbol(new VarSymbol(ident.getValue(), Type.INT,
                false, constExps.size()));
        if (initVal != null) {
            initVal.check();
        }
    }
}
