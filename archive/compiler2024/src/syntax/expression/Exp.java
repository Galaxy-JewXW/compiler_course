package syntax.expression;

import syntax.expression.AddExp;

// 表达式 Exp -> AddExp
public class Exp {
    private final AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public void output() {
        addExp.output();
        System.out.println("<Exp>");
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void check() {
        addExp.check();
    }
}
