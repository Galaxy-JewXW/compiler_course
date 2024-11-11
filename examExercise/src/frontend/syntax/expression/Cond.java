package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;

// 条件表达式 Cond → LOrExp // 存在即可
public class Cond extends SyntaxNode {
    private final LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }

    @Override
    public void print() {
        lOrExp.print();
        System.out.println("<Cond>");
    }
}
