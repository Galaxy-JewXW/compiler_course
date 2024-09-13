package node;

import IO.OutputHandler;
import token.Token;

public class BType {
    //BType → 'int'
    private Token token;

    public BType(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void print() {
        OutputHandler.printToken(token);
    }
}
