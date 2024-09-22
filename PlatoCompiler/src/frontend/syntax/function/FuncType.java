package frontend.syntax.function;

import frontend.syntax.SyntaxNode;
import frontend.token.Token;

// 函数类型 FuncType → 'void' | 'int' | 'char'
// 覆盖三种类型的函数
public class FuncType extends SyntaxNode {
    private final Token funcType;

    public FuncType(Token funcType) {
        this.funcType = funcType;
    }

    public Token getFuncType() {
        return funcType;
    }

    @Override
    public void print() {
        System.out.println(funcType);
        System.out.println("<FuncType>");
    }
}
