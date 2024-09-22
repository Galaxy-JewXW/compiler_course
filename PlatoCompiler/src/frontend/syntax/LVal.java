package frontend.syntax;

import frontend.TableManager;
import frontend.symbol.FuncSymbol;
import frontend.symbol.Symbol;
import frontend.symbol.VarSymbol;
import frontend.syntax.expression.Exp;
import frontend.token.Token;
import frontend.token.TokenType;

// 左值表达式 LVal → Ident ['[' Exp ']'] //1.普通变量、常量 2.一维数组
public class LVal extends SyntaxNode implements Calculable {
    private final Token ident;
    private final Exp exp;

    public LVal(Token ident, Exp exp) {
        this.ident = ident;
        this.exp = exp;
    }

    public LVal(Token ident) {
        this.ident = ident;
        this.exp = null;
    }

    public Token getIdent() {
        return ident;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        System.out.println(ident);
        if (exp != null) {
            System.out.println(TokenType.printType(TokenType.LBRACK));
            exp.print();
            System.out.println(TokenType.printType(TokenType.RBRACK));
        }
        System.out.println("<LVal>");
    }

    @Override
    public int calculate() {
        String name = ident.getContent();
        Symbol symbol = TableManager.getInstance().getSymbol(name);
        if (symbol == null) {
            return 0;
        } else if (symbol instanceof FuncSymbol) {
            throw new RuntimeException("Shouldn't reach here");
        }
        int length = 0;
        if (exp != null) {
            length = exp.calculate();
        }
        VarSymbol varSymbol = (VarSymbol) symbol;
        if (varSymbol.getDimension() == 0) {
            return varSymbol.getConstValue();
        } else {
            return varSymbol.getConstValue(length);
        }
    }
}
