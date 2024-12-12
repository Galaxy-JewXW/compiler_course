package frontend.syntax.variable;

import frontend.syntax.SyntaxNode;
import frontend.syntax.expression.ConstExp;
import frontend.token.Token;
import frontend.token.TokenType;

// 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal | Ident = getint ()
// 包含普通常量、一维数组定义
public class VarDef extends SyntaxNode {
    private final Token ident;
    private final ConstExp constExp;
    private final InitVal initVal;
    private final boolean isGetint;

    public VarDef(Token ident, ConstExp constExp, InitVal initVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
        this.isGetint = false;
    }

    public VarDef(Token ident) {
        this.ident = ident;
        this.constExp = null;
        this.initVal = null;
        this.isGetint = true;
    }

    public boolean isGetint() {
        return isGetint;
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
        if (isGetint) {
            System.out.println(TokenType.printType(TokenType.ASSIGN));
            System.out.println(TokenType.printType(TokenType.GETINTTK));
            System.out.println(TokenType.printType(TokenType.LPARENT));
            System.out.println(TokenType.printType(TokenType.RPARENT));
            System.out.println("<VarDef>");
            return;
        }
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
