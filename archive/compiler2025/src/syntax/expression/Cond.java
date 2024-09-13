package syntax.expression;

import syntax.expression.LOrExp;

// 条件表达式 Cond -> LOrExp
public class Cond {
    private final LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }

    public void output() {
        lOrExp.output();
        System.out.println("<Cond>");
    }

    public void check() {
        lOrExp.check();
    }
}
