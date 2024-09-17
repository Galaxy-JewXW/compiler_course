package frontend.syntax.variable;

import frontend.syntax.SyntaxNode;
import frontend.syntax.expression.ConstExp;
import frontend.token.Token;
import frontend.token.TokenType;

// 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
// 包含普通常量、一维数组定义
public class VarDef extends SyntaxNode {
    private final Token ident;
    private final ConstExp constExp;
    private final InitVal initVal;

    public VarDef(Token ident, ConstExp constExp, InitVal initVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
    }

    public Token getIdent() {
        return ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public InitVal getInitVal() {
        return initVal;
    }

    @Override
    public void print() {
        System.out.println(ident);
        if (constExp != null) {
            System.out.println(TokenType.printType(TokenType.LBRACK));
            constExp.print();
            System.out.println(TokenType.printType(TokenType.RBRACK));
        }
        if (initVal != null) {
            System.out.println(TokenType.printType(TokenType.ASSIGN));
            initVal.print();
        }
        System.out.println("<VarDef>");
    }
}
