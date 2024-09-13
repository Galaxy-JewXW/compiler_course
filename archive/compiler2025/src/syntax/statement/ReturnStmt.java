package syntax.statement;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.Token;
import frontend.TokenType;
import syntax.expression.Exp;

public class ReturnStmt implements Stmt {
    private final Token token;
    private final Exp exp;

    public ReturnStmt(Token token, Exp exp) {
        this.token = token;
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output() {
        System.out.println(token);
        if (exp != null) {
            exp.output();
        }
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (errorVisitor.inVoidFunc() && exp != null) {
            errorVisitor.addError(new ErrorLog(ErrorType.ReturnTypeError,
                    token.getLine()));
        }
        if (exp != null) {
            exp.check();
        }
    }
}
