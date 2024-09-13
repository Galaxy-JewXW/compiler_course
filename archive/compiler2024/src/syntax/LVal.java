package syntax;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.Token;
import frontend.TokenType;
import syntax.expression.Exp;

import java.util.ArrayList;

// 左值表达式 LVal -> Ident {'[' Exp ']'}
public class LVal {
    private final Token ident;
    private final ArrayList<Exp> exps;

    public LVal(Token ident, ArrayList<Exp> exps) {
        this.ident = ident;
        this.exps = exps;
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public void output() {
        System.out.println(ident);
        for (Exp exp : exps) {
            System.out.println(TokenType.LBRACK.name() + " " + TokenType.LBRACK);
            exp.output();
            System.out.println(TokenType.RBRACK.name() + " " + TokenType.RBRACK);
        }
        System.out.println("<LVal>");
    }

    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (!errorVisitor.symbolDefined(ident.getValue())) {
            errorVisitor.addError(new ErrorLog(ErrorType.IdentUndefined,
                    ident.getLine()));
        }
        for (Exp exp : exps) {
            exp.check();
        }
    }
}
