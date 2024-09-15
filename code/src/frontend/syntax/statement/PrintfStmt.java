package frontend.syntax.statement;

import frontend.Token;
import frontend.TokenType;
import frontend.syntax.expression.Exp;

import java.util.ArrayList;

public class PrintfStmt extends Stmt {
    private final Token token; // "printf"
    private final Token stringConst;
    private final ArrayList<Exp> exps;

    public PrintfStmt(Token token, Token stringConst, ArrayList<Exp> exps) {
        this.token = token;
        this.stringConst = stringConst;
        this.exps = exps;
    }

    public Token getToken() {
        return token;
    }

    public Token getStringConst() {
        return stringConst;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.PRINTFTK));
        System.out.println(TokenType.printType(TokenType.LPARENT));
        System.out.println(stringConst);
        for (Exp exp : exps) {
            System.out.println(TokenType.printType(TokenType.COMMA));
            exp.print();
        }
        System.out.println(TokenType.printType(TokenType.RPARENT));
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<Stmt>");
    }
}
