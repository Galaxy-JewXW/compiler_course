package node.func;

import IO.OutputHandler;
import token.Token;

public class FuncType {
    //FuncType → 'void' | 'int'
    private Token funcType;

    public FuncType(Token funcType) {
        this.funcType = funcType;
    }

    public Token getFuncType() {
        return funcType;
    }

    public void print() {
        OutputHandler.printToken(funcType);
        OutputHandler.println("<FuncType>");
    }
}
