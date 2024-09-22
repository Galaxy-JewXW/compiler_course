package frontend.syntax.variable;

import frontend.syntax.SyntaxNode;
import frontend.syntax.expression.Exp;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.ArrayList;

// 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
// 1.表达式初值 2.一维数组初值
public class InitVal extends SyntaxNode {
    private final Exp exp;
    private final ArrayList<Exp> exps;
    private final Token stringConst;

    public InitVal(Exp exp) {
        this.exp = exp;
        this.exps = null;
        this.stringConst = null;
    }

    public InitVal(ArrayList<Exp> exps) {
        this.exp = null;
        this.exps = exps;
        this.stringConst = null;
    }

    public InitVal(Token stringConst) {
        this.exp = null;
        this.exps = null;
        this.stringConst = stringConst;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public Token getStringConst() {
        return stringConst;
    }

    @Override
    public void print() {
        if (exp != null) {
            exp.print();
        } else if (stringConst != null) {
            System.out.println(stringConst);
        } else {
            System.out.println(TokenType.printType(TokenType.LBRACE));
            if (exps != null) {
                for (int i = 0; i < exps.size(); i++) {
                    if (i > 0) {
                        System.out.println(TokenType.printType(TokenType.COMMA));
                    }
                    exps.get(i).print();
                }
            }
            System.out.println(TokenType.printType(TokenType.RBRACE));
        }
        System.out.println("<InitVal>");
    }
}
