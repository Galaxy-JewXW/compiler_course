package node.expression;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.func.FuncRParams;
import error.symbol.*;
import token.Token;
import token.TokenType;

public class UnaryExp {
    //PrimaryExp | Ident '(' [FuncRParams] ')'
    // | UnaryOp UnaryExp
    private PrimaryExp primaryExp;
    private Token Ident;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public UnaryExp(Token Ident, FuncRParams funcRParams) {
        this.Ident = Ident;
        this.funcRParams = funcRParams;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public Token getIdent() {
        return Ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public void print() {
        if (primaryExp != null) {
            primaryExp.print();
        } else if (Ident != null) {
            OutputHandler.printToken(Ident);
            OutputHandler.printToken(TokenType.LPARENT);
            if (funcRParams != null) {
                funcRParams.print();
            }
            OutputHandler.printToken(TokenType.RPARENT);
        } else {
            unaryOp.print();
            unaryExp.print();
        }
        OutputHandler.println("<UnaryExp>");
    }

    public void checkError() {
        if (primaryExp != null) {
            primaryExp.checkError();
        } else if (Ident != null) {
            if (funcRParams != null) {
                funcRParams.checkError();
            }
            if (!ErrorHandler.getInstance().defined(Ident.getToken())) {
                ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.c, Ident.getLine()));
                return;
            }
            //没有定义int a; a()这样的错误;暂时忽略;
            FuncSymbol symbol = (FuncSymbol) ErrorHandler.getInstance().getSymbol(Ident.getToken());
            //参数个数检查
            if (checkParamNum(symbol)) {
                return;
            }
            //参数类型匹配检查
            checkParamType(symbol);
        } else {
            unaryExp.checkError();
        }
    }

    private boolean checkParamNum(FuncSymbol symbol) {
        if (funcRParams == null) {
            if (!symbol.getFuncParams().isEmpty()) {
                ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.d, Ident.getLine()));
                return true;
            }
        } else if (symbol.getFuncParams().size() != funcRParams.getExps().size()) {
            ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.d, Ident.getLine()));
            return true;
        }
        return false;
    }

    private void checkParamType(FuncSymbol funcSymbol) {
        if (funcRParams != null) {
            for (int i = 0; i < funcSymbol.getFuncParams().size(); i++) {
                FuncParam funcParam = ErrorHandler.getInstance().expParam(funcRParams.getExps().get(i));
                if (funcParam != null) {
                    //没有出现int a; a()这样的错误;
                    int dimension;
                    if (funcParam.getName() == null) {//Number
                        dimension = 0;
                    } else {
                        Symbol symbol = ErrorHandler.getInstance().getSymbol(funcParam.getName());
                        if (symbol.getType() != Type.INT) {
                            dimension = -1;//传入void 类型
                        } else if (symbol instanceof VarSymbol) {
                            dimension = ((VarSymbol) symbol).getDimension() - funcParam.getDimension();
                        } else {
                            dimension = 0;
                        }
                    }
                    if (funcSymbol.getFuncParams().get(i).getDimension() != dimension) {
                        ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.e, Ident.getLine()));
                    }
                }
            }
        }
    }
}
