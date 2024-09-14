package frontend.syntax.statement;

import frontend.TokenType;
import frontend.syntax.expression.Cond;

// 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
public class ForStruct extends Stmt {
    private final ForStmt forStmt1;
    private final Cond cond;
    private final ForStmt forStmt2;
    private final Stmt stmt;

    public ForStruct(ForStmt forStmt1, Cond cond, ForStmt forStmt2, Stmt stmt) {
        this.forStmt1 = forStmt1;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.stmt = stmt;
    }

    public ForStmt getForStmt1() {
        return forStmt1;
    }

    public Cond getCond() {
        return cond;
    }

    public ForStmt getForStmt2() {
        return forStmt2;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.FORTK));
        System.out.println(TokenType.printType(TokenType.LPARENT));
        if (forStmt1 != null) {
            forStmt1.print();
        }
        System.out.println(TokenType.printType(TokenType.SEMICN));
        if (cond != null) {
            cond.print();
        }
        System.out.println(TokenType.printType(TokenType.SEMICN));
        if (forStmt2 != null) {
            forStmt2.print();
        }
        System.out.println(TokenType.printType(TokenType.RPARENT));
        stmt.print();
        System.out.println("<Stmt>");
    }
}
