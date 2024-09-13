package syntax;

import frontend.TokenType;
import syntax.expression.Exp;

import java.util.ArrayList;

// 变量初值 InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}'
public class InitVal {
    private final Exp exp;
    private final ArrayList<InitVal> initVals;
    
    public InitVal(Exp exp) {
        this.exp = exp;
        this.initVals = null;
    }
    
    public InitVal(ArrayList<InitVal> initVals) {
        this.exp = null;
        this.initVals = initVals;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }

    public void output() {
        if (exp != null) {
            exp.output();
        } else {
            System.out.println(TokenType.LBRACE.name() + " " + TokenType.LBRACE);
            if (initVals != null && initVals.get(0) != null) {
                initVals.get(0).output();
            }
            if (initVals != null) {
                for (int i = 1; i < initVals.size(); i++) {
                    System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
                    initVals.get(i).output();
                }
            }
            System.out.println(TokenType.RBRACE.name() + " " + TokenType.RBRACE);
        }
        System.out.println("<InitVal>");
    }

    public void check() {
        if (exp != null) {
            exp.check();
        } else if (initVals != null) {
            for (InitVal initVal : initVals) {
                initVal.check();
            }
        }
    }
}
