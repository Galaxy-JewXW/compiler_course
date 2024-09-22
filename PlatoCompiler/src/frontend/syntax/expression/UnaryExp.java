package frontend.syntax.expression;

import frontend.syntax.Calculable;
import frontend.syntax.SyntaxNode;
import frontend.syntax.function.FuncRParams;
import frontend.token.Token;
import frontend.token.TokenType;

// 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
public class UnaryExp extends SyntaxNode implements Calculable {
    private final PrimaryExp primaryExp;
    private final Token ident;
    private final FuncRParams funcRParams;
    private final UnaryOp unaryOp;
    private final UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
        this.ident = null;
        this.funcRParams = null;
        this.unaryOp = null;
        this.unaryExp = null;
    }

    public UnaryExp(Token ident, FuncRParams funcRParams) {
        this.primaryExp = null;
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.unaryOp = null;
        this.unaryExp = null;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.primaryExp = null;
        this.ident = null;
        this.funcRParams = null;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    @Override
    public void print() {
        if (primaryExp != null) {
            primaryExp.print();
        } else if (ident != null) {
            System.out.println(ident);
            System.out.println(TokenType.printType(TokenType.LPARENT));
            if (funcRParams != null) {
                funcRParams.print();
            }
            System.out.println(TokenType.printType(TokenType.RPARENT));
        } else if (unaryOp != null && unaryExp != null) {
            unaryOp.print();
            unaryExp.print();
        }
        System.out.println("<UnaryExp>");
    }

    @Override
    public int calculate() {
        int ans = 0;
        if (unaryOp != null && unaryExp != null) {
            ans = switch (unaryOp.getOperator().getType()) {
                case PLUS -> unaryExp.calculate();
                case MINU -> -unaryExp.calculate();
                case NOT -> unaryExp.calculate() == 0 ? 1 : 0;
                default -> throw new RuntimeException("Shouldn't reach here");
            };
        } else if (primaryExp != null) {
            ans = primaryExp.calculate();
        }
        return ans;
    }
}
