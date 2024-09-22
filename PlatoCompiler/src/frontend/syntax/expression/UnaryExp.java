package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;
import frontend.syntax.function.FuncRParams;
import frontend.token.Token;
import frontend.token.TokenType;

// 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
public class UnaryExp extends SyntaxNode {
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

    public int calculate() {
        int ans = 0;
        if (unaryExp != null && unaryOp != null) {
            switch (unaryOp.getOperator().getType()) {
                case PLUS -> ans = unaryExp.calculate();
                case MINU -> ans = -unaryExp.calculate();
                case NOT -> ans = unaryExp.calculate() == 0 ? 1 : 0;
            }
        } else if (primaryExp != null) {
            ans = primaryExp.calculate();
        }
        return ans;
    }
}
