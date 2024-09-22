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
        String constChar = charConst.getContent();
        if (constChar.length() == 3) {
            return constChar.charAt(1);
        } else {
            return switch (constChar.charAt(2)) {
                case 'a' -> 7;
                case 'b' -> 8;
                case 't' -> 9;
                case 'n' -> 10;
                case 'v' -> 11;
                case 'f' -> 12;
                case '\"' -> 34;
                case '\'' -> 39;
                case '\\' -> 92;
                case '0' -> 0;
                default -> throw new RuntimeException("Invalid character '" + constChar + "'");
            };
        }
    }

    @Override
    public void print() {
        System.out.println(charConst);
        System.out.println("<Character>");
    }
}
