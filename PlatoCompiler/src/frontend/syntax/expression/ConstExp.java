package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;

public class ConstExp extends SyntaxNode {
    private final AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public void print() {
        addExp.print();
        System.out.println("<ConstExp>");
    }

    public int calculate() {
        return addExp.calculate();
    }
}
