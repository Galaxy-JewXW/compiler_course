package frontend.syntax;

import frontend.token.Token;

public class Number extends SyntaxNode implements Calculable {
    private final Token intConst;

    public Number(Token intConst) {
        this.intConst = intConst;
    }

    public Token getIntConst() {
        return intConst;
    }

    public int getIntConstValue() {
        int value;
        try {
            value = Integer.parseInt(intConst.getContent());
        } catch (NumberFormatException e) {
            value = Integer.MIN_VALUE;
        }
        return value;
    }

    @Override
    public int calculate() {
        return getIntConstValue();
    }

    @Override
    public void print() {
        System.out.println(intConst);
        System.out.println("<Number>");
    }
}
