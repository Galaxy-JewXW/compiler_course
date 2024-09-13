package syntax.expression;

import frontend.TokenType;
import syntax.LVal;
import syntax.Number;

// 基本表达式 PrimaryExp -> '(' Exp ')' | LVal | Number
public class PrimaryExp {
    private final Exp exp;
    private final LVal lVal;
    private final Number number;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
        this.lVal = null;
        this.number = null;
    }

    public PrimaryExp(LVal lVal) {
        this.exp = null;
        this.lVal = lVal;
        this.number = null;
    }

    public PrimaryExp(Number number) {
        this.exp = null;
        this.lVal = null;
        this.number = number;
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Number getNumber() {
        return number;
    }

    public void output() {
        if (exp != null) {
            System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
            exp.output();
            System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        } else if (lVal != null) {
            lVal.output();
        } else if (number != null) {
            number.output();
        }
        System.out.println("<PrimaryExp>");
    }

    public void check() {
        if (exp != null) {
            exp.check();
        } else if (lVal != null) {
            lVal.check();
        }
    }
}
