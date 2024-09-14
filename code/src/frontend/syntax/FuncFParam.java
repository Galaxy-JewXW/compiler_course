package frontend.syntax;

import frontend.Token;
import frontend.TokenType;

// 函数形参 FuncFParam → BType Ident ['[' ']']
// 1.普通变量 2.一维数组变量
public class FuncFParam extends SyntaxNode {
    private final BType bType;
    private final Token ident;
    private final boolean isArray;

    public FuncFParam(BType bType, Token ident, boolean isArray) {
        this.bType = bType;
        this.ident = ident;
        this.isArray = isArray;
    }

    public BType getBType() {
        return bType;
    }

    public Token getIdent() {
        return ident;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public void print() {
        bType.print();
        System.out.println(ident);
        if (isArray) {
            System.out.println(TokenType.printType(TokenType.LBRACK));
            System.out.println(TokenType.printType(TokenType.RBRACK));
        }
        System.out.println("<FuncFParam>");
    }
}
