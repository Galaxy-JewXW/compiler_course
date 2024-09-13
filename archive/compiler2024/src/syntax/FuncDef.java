package syntax;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import error.FuncParam;
import error.FuncSymbol;
import frontend.Token;
import frontend.TokenType;

import java.util.ArrayList;

// 函数定义 FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
public class FuncDef {
    private final FuncType funcType;
    private final Token ident;
    private final FuncFParams funcFParams;
    private final Block block;

    public FuncDef(FuncType funcType, Token ident,
                   FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    public void output() {
        funcType.output();
        System.out.println(ident);
        System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
        if (funcFParams != null) {
            funcFParams.output();
        }
        System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        block.output();
        System.out.println("<FuncDef>");
    }

    public void check() {
        FuncParam.Type type = funcType.getType() ==
                TokenType.INTTK ? FuncParam.Type.INT : FuncParam.Type.VOID;
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (errorVisitor.inCurrentTable(ident.getValue())) {
            errorVisitor.addError(new ErrorLog(ErrorType.IdentRedefined, ident.getLine()));
            return;
        }
        ArrayList<FuncParam> funcParams = new ArrayList<>();
        if (funcFParams != null) {
            for (FuncFParam funcFParam : funcFParams.getParams()) {
                funcParams.add(new FuncParam(funcFParam.getIdent().getValue(),
                        FuncParam.Type.INT, funcFParam.getDimension()));
            }
        }
        errorVisitor.addSymbol(new FuncSymbol(ident.getValue(), type, funcParams));
        errorVisitor.addTable(type);
        if (funcFParams != null) {
            funcFParams.check();
        }
        block.check();
        errorVisitor.removeTable();
    }
}
