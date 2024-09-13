package node;

import IO.OutputHandler;
import token.Token;

public class Number {
    //Number → IntConst
    private Token intConst;

    public Number(Token intConst) {
        this.intConst = intConst;
    }

    public void print() {
        OutputHandler.printToken(intConst);
        OutputHandler.println("<Number>");
    }

    public int getVal() {
        return Integer.parseInt(intConst.getToken());
    }
}
