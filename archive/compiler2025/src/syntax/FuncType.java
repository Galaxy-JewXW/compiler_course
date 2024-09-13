package syntax;

import frontend.Token;
import frontend.TokenType;

// 函数类型 FuncType -> 'void' | 'int'
public class FuncType {
    private final Token funcType;

    public FuncType(Token funcType) {
        this.funcType = funcType;
    }

    public TokenType getType() {
        return funcType.getType();
    }

    public void output() {
        System.out.println(funcType);
        System.out.println("<FuncType>");
    }
}