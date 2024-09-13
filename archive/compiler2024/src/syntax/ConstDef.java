package syntax;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import error.FuncParam;
import error.VarSymbol;
import frontend.Token;
import frontend.TokenType;
import syntax.expression.ConstExp;

import java.util.ArrayList;

// 常数定义 ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
public class ConstDef {
    private final Token ident;
    private ArrayList<ConstExp> constExps;
    private final ConstInitVal constInitVal;

    public ConstDef(Token ident, ArrayList<ConstExp> constExps,
                    ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }

    public void output() {
        System.out.println(ident);
        for (ConstExp constExp : constExps) {
            System.out.println(TokenType.LBRACK.name() + " " + TokenType.LBRACK);
            constExp.output();
            System.out.println(TokenType.RBRACK.name() + " " + TokenType.RBRACK);
        }
        System.out.println(TokenType.ASSIGN.name() + " " + TokenType.ASSIGN);
        constInitVal.output();
        System.out.println("<ConstDef>");
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
        errorVisitor.addSymbol(new VarSymbol(ident.getValue(), FuncParam.Type.INT,
                true, constExps.size()));
        constInitVal.check();
    }
}
