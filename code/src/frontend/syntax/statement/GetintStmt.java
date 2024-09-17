package frontend.syntax.statement;

import frontend.syntax.LVal;
import frontend.token.TokenType;

public class GetintStmt extends Stmt {
    private final LVal lVal;

    public GetintStmt(LVal lVal) {
        this.lVal = lVal;
    }

    public LVal getLVal() {
        return lVal;
    }

    @Override
    public void print() {
        lVal.print();
        System.out.println(TokenType.printType(TokenType.ASSIGN));
        System.out.println(TokenType.printType(TokenType.GETINTTK));
        System.out.println(TokenType.printType(TokenType.LPARENT));
        System.out.println(TokenType.printType(TokenType.RPARENT));
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<Stmt>");
    }
}
