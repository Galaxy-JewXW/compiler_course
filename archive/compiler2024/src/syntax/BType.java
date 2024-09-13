package syntax;

import frontend.Token;

public class BType {
    private final Token token;

    public BType(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void output() {
        System.out.println(token);
    }
}
