package frontend.syntax;

import frontend.token.Token;

public class Character extends SyntaxNode {
    private final Token charConst;

    public Character(Token charConst) {
        this.charConst = charConst;
    }

    public Token getCharConst() {
        return charConst;
    }

    public char getCharConstValue() {
        return charConst.getContent().charAt(1);
    }

    @Override
    public void print() {
        System.out.println(charConst);
        System.out.println("<Character>");
    }
}
