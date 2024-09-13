package node.expression;

import IO.OutputHandler;
import token.Token;

public class UnaryOp {
    //UnaryOp → '+' | '−' | '!'
    private Token op;

    public UnaryOp(Token op) {
        this.op = op;
    }

    public void print() {
        OutputHandler.printToken(op);
        OutputHandler.println("<UnaryOp>");
    }

    public Token getOp() {
        return op;
    }
}
