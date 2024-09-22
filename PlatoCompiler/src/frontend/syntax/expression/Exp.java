package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;

// 表达式 Exp → AddExp // 存在即可
public class Exp extends SyntaxNode {
    private final AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public void print() {
        addExp.print();
        System.out.println("<Exp>");
    }
}
