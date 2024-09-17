package frontend.syntax;

import frontend.token.Token;

// 基本类型 BType → 'int' | 'char' // 覆盖两种数据类型的定义
public class BType extends SyntaxNode {
    private final Token token;

    public BType(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        System.out.println(token);
    }
}
