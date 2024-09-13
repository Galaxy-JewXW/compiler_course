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

// 函数形参 FuncFParam -> BType Ident ['[' ']' { '[' ConstExp ']' }]
public class FuncFParam {
    private final BType bType;
    private final Token ident;
    private final ArrayList<ConstExp> constExps;
    private final boolean isArray;

    public FuncFParam(BType bType, Token ident, ArrayList<ConstExp> constExps, boolean isArray) {
        this.bType = bType;
        this.ident = ident;
        this.constExps = constExps;
        this.isArray = isArray;
    }

    public Token getIdent() {
        return ident;
    }

    public int getDimension() {
        return isArray ? constExps.size() + 1 : 0;
    }

    public boolean isArray() {
        return isArray;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public void output() {
        bType.output();
        System.out.println(ident);
        if (isArray) {
            System.out.println(TokenType.LBRACK.name() + " " + TokenType.LBRACK);
            System.out.println(TokenType.RBRACK.name() + " " + TokenType.RBRACK);
            for (ConstExp constExp : constExps) {
                System.out.println(TokenType.LBRACK.name() + " " + TokenType.LBRACK);
                constExp.output();
                System.out.println(TokenType.RBRACK.name() + " " + TokenType.RBRACK);
            }
        }
        System.out.println("<FuncFParam>");
    }

    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (errorVisitor.inCurrentTable(ident.getValue())) {
            errorVisitor.addError(new ErrorLog(ErrorType.IdentRedefined, ident.getLine()));
            return;
        }
        errorVisitor.addSymbol(new VarSymbol(ident.getValue(), FuncParam.Type.INT,
                false, getDimension()));
    }
}
