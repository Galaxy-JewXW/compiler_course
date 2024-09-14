package frontend.syntax;

import frontend.Token;

public class Number extends SyntaxNode {
    private final Token intConst;

    public Number(Token intConst) {
        this.intConst = intConst;
    }

    public Token getIntConst() {
        return intConst;
    }

    public int getIntConstValue() {
        return Integer.parseInt(intConst.getContent());
    }

    @Override
    public void print() {
        System.out.println(intConst);
        System.out.println("<Number>");
    }
}
