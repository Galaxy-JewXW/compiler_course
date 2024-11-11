package frontend.syntax.statement;

import frontend.syntax.LVal;
import frontend.syntax.SyntaxNode;
import frontend.syntax.expression.Exp;
import frontend.token.TokenType;

// 语句 ForStmt → LVal '=' Exp // 存在即可
public class ForStmt extends SyntaxNode {
    private final LVal lVal;
    private final Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public LVal getLVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        lVal.print();
        System.out.println(TokenType.printType(TokenType.ASSIGN));
        exp.print();
        System.out.println("<ForStmt>");
    }
}
