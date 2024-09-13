package syntax.statement;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.TokenType;
import syntax.LVal;
import syntax.expression.Exp;

// 语句 ForStmt -> LVal '=' Exp
public class ForStmt {
    private final LVal lVal;
    private final Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    public void output() {
        lVal.output();
        System.out.println(TokenType.ASSIGN.name() + " " + TokenType.ASSIGN);
        exp.output();
        System.out.println("<ForStmt>");
    }

    public void check() {
        lVal.check();
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (errorVisitor.isConstant(lVal.getIdent().getValue())) {
            errorVisitor.addError(new ErrorLog(ErrorType.ConstAssign,
                    lVal.getIdent().getLine()));
        }
        exp.check();
    }
}
