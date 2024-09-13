package syntax.expression;

import frontend.TokenType;

import java.util.ArrayList;

// 逻辑或表达式 LOrExp -> LAndExp | LOrExp '||' LAndExp
public class LOrExp {
    private final ArrayList<LAndExp> lAndExps;

    public LOrExp(ArrayList<LAndExp> lAndExps) {
        this.lAndExps = lAndExps;
    }

    public ArrayList<LAndExp> getlAndExps() {
        return lAndExps;
    }

    public void output() {
        lAndExps.get(0).output();
        for (int i = 1; i < lAndExps.size(); i++) {
            System.out.println("<LOrExp>");
            System.out.println(TokenType.OR.name() + " " + TokenType.OR);
            lAndExps.get(i).output();
        }
        System.out.println("<LOrExp>");
    }

    public void check() {
        for (LAndExp lAndExp : lAndExps) {
            lAndExp.check();
        }
    }
}
