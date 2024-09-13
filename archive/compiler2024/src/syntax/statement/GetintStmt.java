package syntax.statement;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.TokenType;
import syntax.LVal;

public class GetintStmt implements Stmt {
    private final LVal lVal;

    public GetintStmt(LVal lval) {
        this.lVal = lval;
    }

    public LVal getlVal() {
        return lVal;
    }

    @Override
    public void output() {
        lVal.output();
        System.out.println(TokenType.ASSIGN.name() + " " + TokenType.ASSIGN);
        System.out.println(TokenType.GETINTTK.name() + " " + TokenType.GETINTTK);
        System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
        System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
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
    }
}
