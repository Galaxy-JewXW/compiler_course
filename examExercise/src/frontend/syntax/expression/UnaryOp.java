package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;
import frontend.token.Token;

// 单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
public class UnaryOp extends SyntaxNode {
    private final Token operator;

    public UnaryOp(Token operator) {
        this.operator = operator;
    }

    public Token getOperator() {
        return operator;
    }

    @Override
    public void print() {
        System.out.println(operator);
        System.out.println("<UnaryOp>");
    }
}
