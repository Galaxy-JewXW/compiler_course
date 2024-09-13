package syntax;

import frontend.Token;

// 数值 Number -> IntConst
public class Number {
    private final Token intConst;

    public Number(Token token) {
        this.intConst = token;
    }

    public int getValue() {
        return Integer.parseInt(intConst.getValue());
    }

    public void output() {
        System.out.println(intConst);
        System.out.println("<Number>");
    }
}
