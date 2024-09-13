package syntax.statement;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.TokenType;
import syntax.expression.Exp;
import syntax.LVal;

public class LValExpStmt implements Stmt {
    private final LVal lVal;
    private final Exp exp;

    public LValExpStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output() {
        lVal.output();
        System.out.println(TokenType.ASSIGN.name() + " " + TokenType.ASSIGN);
        exp.output();
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<Stmt>");
    }

    @Override
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
