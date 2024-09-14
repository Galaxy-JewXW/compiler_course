package frontend.syntax;

import frontend.Token;
import frontend.TokenType;
import frontend.syntax.expression.ConstExp;

// 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
// 包含普通变量、一维数组两种情况
public class ConstDef extends SyntaxNode {
    private final Token ident;
    private final ConstExp constExp;
    private final ConstInitVal constInitVal;

    public ConstDef(Token ident, ConstExp constExp, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.constInitVal = constInitVal;
    }

    public Token getIdent() {
        return ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }

    @Override
    public void print() {
        System.out.println(ident);
        if (constExp != null) {
            System.out.println(TokenType.printType(TokenType.LBRACK));
            constExp.print();
            System.out.println(TokenType.printType(TokenType.RBRACK));
        }
        System.out.println(TokenType.printType(TokenType.ASSIGN));
        constInitVal.print();
        System.out.println("<ConstDef>");
    }
}
