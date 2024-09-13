package syntax.expression;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import error.FuncParam;
import error.FuncSymbol;
import error.Symbol;
import error.VarSymbol;
import frontend.Token;
import frontend.TokenType;
import syntax.FuncRParams;

// 一元表达式 UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
public class UnaryExp {
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

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public void output() {
        if (primaryExp != null) {
            primaryExp.output();
        } else if (ident != null) {
            System.out.println(ident);
            System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
            if (funcRParams != null) {
                funcRParams.output();
            }
            System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        } else if (unaryExp != null && unaryOp != null) {
            unaryOp.output();
            unaryExp.output();
        }
        System.out.println("<UnaryExp>");
    }

    public void check() {
        if (primaryExp != null) {
            primaryExp.check();
        } else if (ident != null) {
            if (funcRParams != null) {
                funcRParams.check();
            }
            ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
            if (!errorVisitor.symbolDefined(ident.getValue())) {
                errorVisitor.addError(new ErrorLog(ErrorType.IdentUndefined,
                        ident.getLine()));
                return;
            }
            FuncSymbol funcSymbol = (FuncSymbol) errorVisitor.getSymbol(ident.getValue());
            checkParam(funcSymbol);
        }
    }

    private void checkParam(FuncSymbol funcSymbol) {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (funcRParams == null && !funcSymbol.getParams().isEmpty()) {
            if (ident != null) {
                errorVisitor.addError(new ErrorLog(ErrorType.ParaNumNotMatch,
                        ident.getLine()));
            }
        } else if (funcRParams != null
                && funcSymbol.getParams().size() != funcRParams.getExps().size()) {
            if (ident != null) {
                errorVisitor.addError(new ErrorLog(ErrorType.ParaNumNotMatch,
                        ident.getLine()));
            }
        }

        if (funcRParams != null) {
            for (int i = 0; i < funcSymbol.getParams().size(); i++) {
                FuncParam funcParam = errorVisitor.expParam(funcRParams.getExps().get(i));
                if (funcParam != null) {
                    int dimension;
                    if (funcParam.getName() == null) {
                        dimension = 0;
                    } else {
                        Symbol symbol = errorVisitor.getSymbol(funcParam.getName());
                        if (symbol.getType() != FuncParam.Type.INT) {
                            dimension = -1;
                        } else if (symbol instanceof VarSymbol varSymbol) {
                            dimension = varSymbol.getDimension() - funcParam.getDimension();
                        } else {
                            dimension = 0;
                        }
                    }
                    if (funcSymbol.getParams().get(i).getDimension() != dimension) {
                        if (ident != null) {
                            errorVisitor.addError(new ErrorLog(ErrorType.ParaTypeNotMatch,
                                    ident.getLine()));
                        }
                    }
                }
            }
        }
    }
}
