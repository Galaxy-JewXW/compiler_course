package frontend.syntax;

import frontend.symbol.ConstSymbol;
import frontend.symbol.Symbol;
import frontend.symbol.TableManager;
import frontend.symbol.VarSymbol;
import frontend.syntax.expression.Exp;
import frontend.token.Token;
import frontend.token.TokenType;

// 左值表达式 LVal → Ident ['[' Exp ']'] //1.普通变量、常量 2.一维数组
public class LVal extends SyntaxNode {
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

    public int calculate() {
        Symbol symbol = TableManager.getInstance().getSymbol(ident.getContent());
        if (symbol == null) {
            return 0;
        }
        int length = 0;
        if (exp != null) {
            length = exp.calculate();
        }
        if (symbol instanceof ConstSymbol constSymbol) {
            if (constSymbol.getDimension() == 0) {
                return constSymbol.getConst();
            } else {
                return constSymbol.getConst(length);
            }
        } else if (symbol instanceof VarSymbol varSymbol) {
            if (varSymbol.getDimension() == 0) {
                return varSymbol.getConst();
            } else {
                return varSymbol.getConst(length);
            }
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }
}
