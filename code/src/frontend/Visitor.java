package frontend;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.symbol.FuncParam;
import frontend.symbol.FuncSymbol;
import frontend.symbol.Symbol;
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
import frontend.syntax.FuncRParams;
import frontend.syntax.InitVal;
import frontend.syntax.LVal;
import frontend.syntax.MainFuncDef;
import frontend.syntax.VarDecl;
import frontend.syntax.VarDef;
import frontend.syntax.expression.AddExp;
import frontend.syntax.expression.Cond;
import frontend.syntax.expression.ConstExp;
import frontend.syntax.expression.EqExp;
import frontend.syntax.expression.Exp;
import frontend.syntax.expression.LAndExp;
import frontend.syntax.expression.LOrExp;
import frontend.syntax.expression.MulExp;
import frontend.syntax.expression.PrimaryExp;
import frontend.syntax.expression.RelExp;
import frontend.syntax.expression.UnaryExp;
import frontend.syntax.statement.BlockStmt;
import frontend.syntax.statement.BreakStmt;
import frontend.syntax.statement.ContinueStmt;
import frontend.syntax.statement.ExpStmt;
import frontend.syntax.statement.ForStmt;
import frontend.syntax.statement.ForStruct;
import frontend.syntax.statement.GetcharStmt;
import frontend.syntax.statement.GetintStmt;
import frontend.syntax.statement.IfStmt;
import frontend.syntax.statement.LValExpStmt;
import frontend.syntax.statement.PrintfStmt;
import frontend.syntax.statement.ReturnStmt;
import frontend.syntax.statement.Stmt;
import tools.ToParam;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visitor {
    private final TableManager tableManager = TableManager.getInstance();
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
        }
        // 对于一个名字重定义的函数，也应该完整分析函数内部是否具有其它错误
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

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        tableManager.addSymbol(new FuncSymbol("main", SymbolType.INT32, new ArrayList<>()));
        tableManager.addTable(SymbolType.INT32);
        visitBlock(mainFuncDef.getBlock());
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
        if (stmt instanceof LValExpStmt lValExpStmt) {
            visitLValExpStmt(lValExpStmt);
        } else if (stmt instanceof ExpStmt expStmt) {
            visitExpStmt(expStmt);
        } else if (stmt instanceof BlockStmt blockStmt) {
            visitBlockStmt(blockStmt);
        } else if (stmt instanceof IfStmt ifStmt) {
            visitIfStmt(ifStmt);
        } else if (stmt instanceof ForStruct forStruct) {
            visitForStruct(forStruct);
        } else if (stmt instanceof BreakStmt breakStmt) {
            visitBreakStmt(breakStmt);
        } else if (stmt instanceof ContinueStmt continueStmt) {
            visitContinueStmt(continueStmt);
        } else if (stmt instanceof ReturnStmt returnStmt) {
            visitReturnStmt(returnStmt);
        } else if (stmt instanceof GetintStmt getintStmt) {
            visitGetintStmt(getintStmt);
        } else if (stmt instanceof GetcharStmt getcharStmt) {
            visitGetcharStmt(getcharStmt);
        } else if (stmt instanceof PrintfStmt printfStmt) {
            visitPrintfStmt(printfStmt);
        }
    }

    // 不能改变常量的值
    // LVal为常量的时候，不能对其修改
    private void visitLValExpStmt(LValExpStmt lValExpStmt) {
        visitLVal(lValExpStmt.getLVal());
        if (tableManager.isConstantVarSymbol(
                lValExpStmt.getLVal().getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ConstAssign,
                    lValExpStmt.getLVal().getIdent().getLine()
            ));
        }
        visitExp(lValExpStmt.getExp());
    }

    private void visitExpStmt(ExpStmt expStmt) {
        if (expStmt.getExp() != null) {
            visitExp(expStmt.getExp());
        }
    }

    private void visitBlockStmt(BlockStmt blockStmt) {
        tableManager.addTable(null);
        visitBlock(blockStmt.getBlock());
        tableManager.popTable();
    }

    private void visitIfStmt(IfStmt ifStmt) {
        visitCond(ifStmt.getCond());
        visitStmt(ifStmt.getStmt1());
        if (ifStmt.getStmt2() != null) {
            visitStmt(ifStmt.getStmt2());
        }
    }

    private void visitForStruct(ForStruct forStruct) {
        if (forStruct.getForStmt1() != null) {
            visitForStmt(forStruct.getForStmt1());
        }
        if (forStruct.getCond() != null) {
            visitCond(forStruct.getCond());
        }
        if (forStruct.getForStmt2() != null) {
            visitForStmt(forStruct.getForStmt2());
        }
        tableManager.enterLoop();
        visitStmt(forStruct.getStmt());
        tableManager.exitLoop();
    }

    private void visitForStmt(ForStmt forStmt) {
        visitLVal(forStmt.getLVal());
        if (tableManager.isConstantVarSymbol(
                forStmt.getLVal().getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ConstAssign,
                    forStmt.getLVal().getIdent().getLine()
            ));
        }
        visitExp(forStmt.getExp());
    }

    // 在非循环块中使用break和continue语句
    private void visitBreakStmt(BreakStmt breakStmt) {
        if (tableManager.notInLoop()) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.BreakContinueNotInLoop,
                    breakStmt.getToken().getLine()
            ));
        }
    }

    private void visitContinueStmt(ContinueStmt continueStmt) {
        if (tableManager.notInLoop()) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.BreakContinueNotInLoop,
                    continueStmt.getToken().getLine()
            ));
        }
    }

    // 无返回值的函数存在不匹配的return语句
    private void visitReturnStmt(ReturnStmt returnStmt) {
        if (tableManager.inVoidFunc() && returnStmt.getExp() != null) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ReturnTypeMismatch,
                    returnStmt.getToken().getLine()
            ));
            return;
        }
        if (returnStmt.getExp() != null) {
            visitExp(returnStmt.getExp());
        }
    }

    private void visitGetintStmt(GetintStmt getintStmt) {
        visitLVal(getintStmt.getLVal());
        if (tableManager.isConstantVarSymbol(
                getintStmt.getLVal().getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ConstAssign,
                    getintStmt.getLVal().getIdent().getLine()
            ));
        }
    }

    private void visitGetcharStmt(GetcharStmt getcharStmt) {
        visitLVal(getcharStmt.getLVal());
        if (tableManager.isConstantVarSymbol(
                getcharStmt.getLVal().getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ConstAssign,
                    getcharStmt.getLVal().getIdent().getLine()
            ));
        }
    }

    // printf中格式字符与表达式个数不匹配
    private void visitPrintfStmt(PrintfStmt printfStmt) {
        int res = 0;
        Pattern pattern = Pattern.compile("%[cd]");  // 同时匹配%d和%c
        Matcher matcher = pattern.matcher(printfStmt.getStringConst().getContent());
        while (matcher.find()) {
            res++;
        }
        if (res != printfStmt.getExps().size()) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.PrintfFmtCntNotMatch,
                    printfStmt.getToken().getLine()
            ));
        }
    }

    private void visitExp(Exp exp) {
        visitAddExp(exp.getAddExp());
    }

    private void visitCond(Cond cond) {
        visitLOrExp(cond.getLOrExp());
    }

    // 未定义的名字
    private void visitLVal(LVal lVal) {
        if (tableManager.getSymbol(lVal.getIdent().getContent()) == null) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentUndefined,
                    lVal.getIdent().getLine()
            ));
        }
        if (lVal.getExp() != null) {
            visitExp(lVal.getExp());
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp());
        } else if (primaryExp.getLVal() != null) {
            visitLVal(primaryExp.getLVal());
        }
    }

    private void visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryExp() != null) {
            visitUnaryExp(unaryExp.getUnaryExp());
        } else if (unaryExp.getIdent() != null) {
            /*
              该分支对应Ident '(' [FuncRParams] ')'
              可能出现的错误有：
              1.未定义的名字
              2.函数参数个数不匹配
              3.函数参数类型不匹配
             */
            if (unaryExp.getFuncRParams() != null) {
                visitFuncRParams(unaryExp.getFuncRParams());
            }
            if (tableManager.getSymbol(unaryExp.getIdent().getContent()) == null) {
                ErrorHandler.getInstance().addError(new Error(
                        ErrorType.IdentUndefined,
                        unaryExp.getIdent().getLine()
                ));
                return;
            }
            FuncSymbol funcSymbol = (FuncSymbol) tableManager.getSymbol(
                    unaryExp.getIdent().getContent());
            checkParamError(unaryExp, funcSymbol);
        }
    }

    private void checkParamError(UnaryExp unaryExp, FuncSymbol funcSymbol) {
        // 检查在Ident '(' [FuncRParams] ')'形式下可能出现的问题
        // 首先检查函数参数个数是否匹配
        if (unaryExp.getFuncRParams() == null && !funcSymbol.getFuncParams().isEmpty()) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ParamSizeMismatch,
                    unaryExp.getIdent().getLine()
            ));
            return;
        } else if (unaryExp.getFuncRParams() != null
                && (unaryExp.getFuncRParams().getExps().size()
                != funcSymbol.getFuncParams().size())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.ParamSizeMismatch,
                    unaryExp.getIdent().getLine()
            ));
            return;
        }
        /*
        接着检查函数参数类型是否匹配
        一共存在四种类型的不匹配情况：
        1. 传递数组给变量。
        2. 传递变量给数组。
        3. 传递 char 型数组给 int 型数组。
        4. 传递 int 型数组给 char 型数组。
         */
        if (unaryExp.getFuncRParams() != null) {
            for (int i = 0; i < funcSymbol.getFuncParams().size(); i++) {
                FuncParam funcParam = ToParam.expToParam(
                        unaryExp.getFuncRParams().getExps().get(i));
                int dimension;
                if (funcParam.getName() == null) {
                    dimension = 0;
                } else {
                    Symbol symbol = tableManager.getSymbol(funcParam.getName());
                    if (symbol.getType() == SymbolType.VOID) {
                        dimension = -1;
                    } else if (symbol instanceof VarSymbol varSymbol) {
                        /*
                        举例说明：
                        一维数组名为实参，如a[2], foo(a)，则此时funcParam将其识别为0维的左值
                        从符号表查询得到a是1维，相减得这里的实参维数是1
                         */
                        dimension = varSymbol.getDimension() - funcParam.getDimension();
                    } else {
                        dimension = 0;
                    }
                }
                // 这里处理前两种错误，归纳为形参与实参的维数不符
                if (funcSymbol.getFuncParams().get(i).getDimension() != dimension) {
                    ErrorHandler.getInstance().addError(new Error(
                            ErrorType.ParamTypeMismatch,
                            unaryExp.getIdent().getLine()
                    ));
                    continue;
                }
                // 这里处理后两种错误
                if (funcSymbol.getFuncParams().get(i).getDimension() == 1
                        || dimension == 1) {
                    // 两者均是数组
                    if (funcSymbol.getFuncParams().get(i).getType() != funcParam.getType()) {
                        ErrorHandler.getInstance().addError(new Error(
                                ErrorType.ParamTypeMismatch,
                                unaryExp.getIdent().getLine()
                        ));
                    }
                }
            }
        }
    }

    private void visitFuncRParams(FuncRParams funcRParams) {
        for (Exp exp : funcRParams.getExps()) {
            visitExp(exp);
        }
    }

    private void visitMulExp(MulExp mulExp) {
        for (UnaryExp unaryExp : mulExp.getUnaryExps()) {
            visitUnaryExp(unaryExp);
        }
    }

    private void visitAddExp(AddExp addExp) {
        for (MulExp mulExp : addExp.getMulExps()) {
            visitMulExp(mulExp);
        }
    }

    private void visitRelExp(RelExp relExp) {
        for (AddExp addExp : relExp.getAddExps()) {
            visitAddExp(addExp);
        }
    }

    private void visitEqExp(EqExp eqExp) {
        for (RelExp relExp : eqExp.getRelExps()) {
            visitRelExp(relExp);
        }
    }

    private void visitLAndExp(LAndExp lAndExp) {
        for (EqExp eqExp : lAndExp.getEqExps()) {
            visitEqExp(eqExp);
        }
    }

    private void visitLOrExp(LOrExp lOrExp) {
        for (LAndExp lAndExp : lOrExp.getlAndExps()) {
            visitLAndExp(lAndExp);
        }
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }
}
