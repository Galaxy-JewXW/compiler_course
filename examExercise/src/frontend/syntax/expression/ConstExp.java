package frontend.syntax.expression;

import frontend.syntax.Calculable;
import frontend.syntax.SyntaxNode;

public class ConstExp extends SyntaxNode implements Calculable {
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

    @Override
    public int calculate() {
        return addExp.calculate();
    }
}
