package syntax.expression;

import syntax.expression.AddExp;

// 常量表达式 ConstExp -> AddExp
public class ConstExp {
    private final AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void output() {
        addExp.output();
        System.out.println("<ConstExp>");
    }

    public void check() {
        addExp.check();
    }
}
