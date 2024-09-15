package frontend;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.symbol.FuncParam;
import frontend.symbol.FuncSymbol;
import frontend.symbol.SymbolType;
import frontend.symbol.VarSymbol;
import frontend.syntax.Block;
import frontend.syntax.BlockItem;
import frontend.syntax.CompUnit;
import frontend.syntax.ConstDecl;
import frontend.syntax.ConstDef;
import frontend.syntax.ConstInitVal;
import frontend.syntax.Decl;
import frontend.syntax.FuncDef;
import frontend.syntax.FuncFParam;
import frontend.syntax.FuncFParams;
import frontend.syntax.InitVal;
import frontend.syntax.VarDecl;
import frontend.syntax.VarDef;
import frontend.syntax.expression.ConstExp;
import frontend.syntax.expression.Exp;
import frontend.syntax.statement.ReturnStmt;
import frontend.syntax.statement.Stmt;

import java.util.ArrayList;

public class Visitor {
    private final TableManager tableManager = new TableManager();
    private final CompUnit compUnit;

    public Visitor(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void visit() {
        visitCompUnit();
    }

    private void visitCompUnit() {
        for (Decl decl : compUnit.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(compUnit.getMainFuncDef());
    }

    private void visitDecl(Decl decl) {
        if (decl instanceof ConstDecl constDecl) {
            visitConstDecl(constDecl);
        } else if (decl instanceof VarDecl varDecl) {
            visitVarDecl(varDecl);
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            visitConstDef(constDef, constDecl.getBType().getToken().getType());
        }
    }

    // 名字重定义
    private void visitConstDef(ConstDef constDef, TokenType type) {
        if (tableManager.inCurrentTable(constDef.getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentRedefined, constDef.getIdent().getLine()
            ));
            return;
        }
        if (constDef.getConstExp() != null) {
            visitConstExp(constDef.getConstExp());
        }
        tableManager.addSymbol(new VarSymbol(
                constDef.getIdent().getContent(),
                type == TokenType.INTTK ? SymbolType.INT32 : SymbolType.INT8,
                true,
                constDef.getConstExp() == null ? 0 : 1
        ));
        visitConstInitVal(constDef.getConstInitVal());
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() != null) {
            visitConstExp(constInitVal.getConstExp());
        } else if (constInitVal.getConstExps() != null) {
            for (ConstExp constExp : constInitVal.getConstExps()) {
                visitConstExp(constExp);
            }
        }
    }

    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            visitVarDef(varDef, varDecl.getBType().getToken().getType());
        }
    }

    // 名字重定义
    private void visitVarDef(VarDef varDef, TokenType type) {
        if (tableManager.inCurrentTable(varDef.getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentRedefined, varDef.getIdent().getLine()
            ));
            return;
        }
        if (varDef.getConstExp() != null) {
            visitConstExp(varDef.getConstExp());
        }
        tableManager.addSymbol(new VarSymbol(
                varDef.getIdent().getContent(),
                type == TokenType.INTTK ? SymbolType.INT32 : SymbolType.INT8,
                false,
                varDef.getConstExp() == null ? 0 : 1
        ));
        if (varDef.getInitVal() != null) {
            visitInitVal(varDef.getInitVal());
        }
    }

    private void visitInitVal(InitVal initVal) {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp());
        } else if (initVal.getExps() != null) {
            for (Exp exp : initVal.getExps()) {
                visitExp(exp);
            }
        }
    }

    /**
     * 可能出现的问题有：
     * 名字重定义
     * 有返回值的函数缺少return语句
     */
    private void visitFuncDef(FuncDef funcDef) {
        SymbolType funcReturnType;
        switch (funcDef.getFuncType().getFuncType().getType()) {
            case INTTK -> funcReturnType = SymbolType.INT32;
            case CHARTK -> funcReturnType = SymbolType.INT8;
            default -> funcReturnType = SymbolType.VOID;
        }
        // 检查名字重定义
        if (tableManager.inCurrentTable(funcDef.getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentRedefined, funcDef.getIdent().getLine()
            ));
            return;
        }
        ArrayList<FuncParam> funcParams = new ArrayList<>();
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                funcParams.add(new FuncParam(
                        funcFParam.getIdent().getContent(),
                        funcFParam.getBType().getToken().getType() ==
                                TokenType.INTTK ? SymbolType.INT32 : SymbolType.INT8,
                        funcFParam.isArray() ? 1 : 0));
            }
        }
        tableManager.addSymbol(new FuncSymbol(
                funcDef.getIdent().getContent(), funcReturnType, funcParams
        ));
        tableManager.addTable(funcReturnType);
        if (funcDef.getFuncFParams() != null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }
        visitBlock(funcDef.getBlock());
        tableManager.popTable();
    }

    private void visitFuncFParams(FuncFParams funcFParams) {
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            visitFuncFParam(funcFParam);
        }
    }

    // 名字重定义
    private void visitFuncFParam(FuncFParam funcFParam) {
        if (tableManager.inCurrentTable(funcFParam.getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentRedefined, funcFParam.getIdent().getLine()
            ));
            return;
        }
        tableManager.addSymbol(new VarSymbol(
                funcFParam.getIdent().getContent(),
                funcFParam.getBType().getToken().getType() ==
                        TokenType.INTTK ? SymbolType.INT32 : SymbolType.INT8,
                false,
                funcFParam.isArray() ? 1 : 0
        ));
    }

    // 在这里检查函数是否有返回值
    private void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
        // 当前符号表是属于一张具有返回值的函数的
        if (tableManager.inReturnValueFunc()) {
            ArrayList<BlockItem> items = block.getBlockItems();
            if (items.isEmpty()
                    || items.get(items.size() - 1).getStmt() == null
                    || !(items.get(items.size() - 1).getStmt() instanceof ReturnStmt)) {
                // int或char函数缺少显性的return with value语句
                ErrorHandler.getInstance().addError(new Error(
                        ErrorType.ReturnMissing, block.getEndLine()
                ));
            }
        }
    }

    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem.getStmt() == null) {
            visitDecl(blockItem.getDecl());
        } else if (blockItem.getDecl() == null) {
            visitStmt(blockItem.getStmt());
        }
    }

    private void visitStmt(Stmt stmt) {

    }

    private void visitExp(Exp exp) {

    }

    private void visitConstExp(ConstExp constExp) {

    }
}
