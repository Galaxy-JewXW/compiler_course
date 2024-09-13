package syntax.statement;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.Token;
import frontend.TokenType;

public class BreakStmt implements Stmt {
    private final Token token;

    public BreakStmt(Token token) {
        this.token = token;
    }

    @Override
    public void output() {
        System.out.println(token);
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (!errorVisitor.isInLoop()) {
            errorVisitor.addError(new ErrorLog(ErrorType.BreakContinueNotInLoop,
                    token.getLine()));
        }
    }
}
