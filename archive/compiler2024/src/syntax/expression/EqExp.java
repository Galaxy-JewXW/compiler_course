package syntax.expression;

import frontend.Token;

import java.util.ArrayList;

// 相等性表达式 EqExp -> RelExp | EqExp ('==' | '!=') RelExp
public class EqExp {
    private final ArrayList<RelExp> relExps;
    private final ArrayList<Token> ops;

    public EqExp(ArrayList<RelExp> relExps, ArrayList<Token> ops) {
        this.relExps = relExps;
        this.ops = ops;
    }

    public ArrayList<RelExp> getRelExps() {
        return relExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void output() {
        relExps.get(0).output();
        for (int i = 1; i < relExps.size(); i++) {
            System.out.println("<EqExp>");
            System.out.println(ops.get(i - 1));
            relExps.get(i).output();
        }
        System.out.println("<EqExp>");
    }

    public void check() {
        for (RelExp relExp : relExps) {
            relExp.check();
        }
    }
}
