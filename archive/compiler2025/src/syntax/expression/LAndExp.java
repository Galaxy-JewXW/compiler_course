package syntax.expression;

import frontend.TokenType;

import java.util.ArrayList;

// 逻辑与表达式 LAndExp -> EqExp | LAndExp '&&' EqExp
public class LAndExp {
    private final ArrayList<EqExp> eqExps;

    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }

    public void output() {
        eqExps.get(0).output();
        for (int i = 1; i < eqExps.size(); i++) {
            System.out.println("<LAndExp>");
            System.out.println(TokenType.AND.name() + " " + TokenType.AND);
            eqExps.get(i).output();
        }
        System.out.println("<LAndExp>");
    }

    public void check() {
        for (EqExp eqExp : eqExps) {
            eqExp.check();
        }
    }
}
