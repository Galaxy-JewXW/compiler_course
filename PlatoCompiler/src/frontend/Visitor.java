package frontend;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.symbol.*;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.*;
import frontend.syntax.statement.*;
import frontend.syntax.variable.*;
import frontend.token.TokenType;
import middle.component.InitialValue;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;
import tools.StrToArray;
import tools.ToParam;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Visitor {
    private final TableManager tableManager = TableManager.getInstance();
    private final CompUnit compUnit;
    private boolean isGlobal = true;

    public Visitor(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void visit() {
        visitCompUnit();
    }

    private void visitCompUnit() {
        isGlobal = true;
        for (Decl decl : compUnit.getDecls()) {
            visitDecl(decl);
        }
        isGlobal = false;
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
        int dimension = 0;
        int length = 0;
        if (constDef.getConstExp() != null) {
            dimension = 1;
            length = constDef.getConstExp().calculate();
        }
        ValueType valueType = switch (type) {
            case INTTK -> IntegerType.i32;
            case CHARTK -> IntegerType.i8;
            default -> throw new RuntimeException("Shouldn't reacn here");
        };
        if (dimension == 1) {
            valueType = new ArrayType(length, valueType);
        }
        ArrayList<Integer> integers = calculateConstInitVal(constDef.getConstInitVal());
        // 如果是char类型的常量or数组，需要对元素进行截断处理
        // 下面解析varDef的流程同理
        if (type == TokenType.CHARTK) {
            integers = integers.stream()
                    .map(i -> i & 0xFF)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        InitialValue initialValue = new InitialValue(valueType, length, integers);
        tableManager.addSymbol(new VarSymbol(
                constDef.getIdent().getContent(),
                type == TokenType.INTTK ? SymbolType.INT : SymbolType.CHAR,
                true,
                constDef.getConstExp() == null ? 0 : 1,
                length,
                initialValue
        ));
        visitConstInitVal(constDef.getConstInitVal());
    }

    private ArrayList<Integer> calculateConstInitVal(ConstInitVal constInitVal) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (constInitVal.getConstExp() != null) {
            ans.add(constInitVal.getConstExp().calculate());
        } else if (constInitVal.getConstExps() != null) {
            for (ConstExp constExp : constInitVal.getConstExps()) {
                ans.add(constExp.calculate());
            }
        } else if (constInitVal.getStringConst() != null) {
            ans.addAll(StrToArray.str2Array(constInitVal.getStringConst().getContent()));
        }
        return ans;
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
        int dimension = 0;
        int length = 0;
        if (varDef.getConstExp() != null) {
            dimension = 1;
            length = varDef.getConstExp().calculate();
        }
        VarSymbol varSymbol;
        if (isGlobal) {
            ValueType valueType = switch (type) {
                case INTTK -> IntegerType.i32;
                case CHARTK -> IntegerType.i8;
                default -> throw new RuntimeException("Shouldn't reacn here");
            };
            if (dimension == 1) {
                valueType = new ArrayType(length, valueType);
            }
            InitialValue initialValue;
            if (varDef.getInitVal() != null) {
                ArrayList<Integer> integers = calculateInitVal(varDef.getInitVal());
                if (type == TokenType.CHARTK) {
                    integers = integers.stream()
                            .map(i -> i & 0xFF)
                            .collect(Collectors.toCollection(ArrayList::new));
                }
                initialValue = new InitialValue(valueType, length, integers);
            } else {
                initialValue = new InitialValue(valueType, length, null);
            }
            varSymbol = new VarSymbol(
                    varDef.getIdent().getContent(),
                    type == TokenType.INTTK ? SymbolType.INT : SymbolType.CHAR,
                    false,
                    varDef.getConstExp() == null ? 0 : 1,
                    length,
                    initialValue
            );
        } else {
            varSymbol = new VarSymbol(
                    varDef.getIdent().getContent(),
                    type == TokenType.INTTK ? SymbolType.INT : SymbolType.CHAR,
                    false,
                    varDef.getConstExp() == null ? 0 : 1,
                    length,
                    null
            );
        }
        tableManager.addSymbol(varSymbol);
        if (varDef.getInitVal() != null) {
            visitInitVal(varDef.getInitVal());
        }
    }

    // 全局定义变量，理论上可以算出其初值
    private ArrayList<Integer> calculateInitVal(InitVal initVal) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (initVal.getExp() != null) {
            ans.add(initVal.getExp().calculate());
        } else if (initVal.getExps() != null) {
            for (Exp exp : initVal.getExps()) {
                ans.add(exp.calculate());
            }
        } else if (initVal.getStringConst() != null) {
            ans.addAll(StrToArray.str2Array(initVal.getStringConst().getContent()));
        }
        return ans;
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
            case INTTK -> funcReturnType = SymbolType.INT;
            case CHARTK -> funcReturnType = SymbolType.CHAR;
            default -> funcReturnType = SymbolType.VOID;
        }
        // 检查名字重定义
        if (tableManager.inCurrentTable(funcDef.getIdent().getContent())) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentRedefined, funcDef.getIdent().getLine()
            ));
        }
        // 对于一个名字重定义的函数，也应该完整分析函数内部是否具有其它错误
        ArrayList<ParamSymbol> paramSymbols = new ArrayList<>();
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                paramSymbols.add(new ParamSymbol(
                        funcFParam.getIdent().getContent(),
                        funcFParam.getBType().getToken().getType() ==
                                TokenType.INTTK ? SymbolType.INT : SymbolType.CHAR,
                        funcFParam.isArray() ? 1 : 0));
            }
        }
        tableManager.addSymbol(new FuncSymbol(
                funcDef.getIdent().getContent(), funcReturnType, paramSymbols
        ));
        // 函数形参不在Block块中，但实际上应该属于Block块中定义的变量
        tableManager.createTable(funcReturnType);
        if (funcDef.getFuncFParams() != null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }
        visitBlock(funcDef.getBlock());
        tableManager.recoverTable();
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        tableManager.addSymbol(new FuncSymbol(
                "main",
                SymbolType.INT,
                new ArrayList<>() // main函数形参表为空
        ));
        tableManager.createTable(SymbolType.INT);
        visitBlock(mainFuncDef.getBlock());
        tableManager.recoverTable();
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
                        TokenType.INTTK ? SymbolType.INT : SymbolType.CHAR,
                false,
                funcFParam.isArray() ? 1 : 0,
                funcFParam.isArray() ? -1 : 0,
                null
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
                // 这里并不关心返回的值是int型还是char型
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
        // 访问Block中的Block时，需要另外创建符号表
        tableManager.createTable(null);
        visitBlock(blockStmt.getBlock());
        // 递归访问完Block之后，切换符号表
        tableManager.recoverTable();
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
                ParamSymbol paramSymbol = ToParam.expToParam(
                        unaryExp.getFuncRParams().getExps().get(i));
                if (paramSymbol == null) {
                    continue;
                }
                int dimension;
                if (paramSymbol.getName() == null) {
                    dimension = 0;
                } else {
                    Symbol symbol = tableManager.getSymbol(paramSymbol.getName());
                    if (symbol.getType() == SymbolType.VOID) {
                        // void类型的函数返回值如果作为参数进行传递（传递到一个如需要int的函数参数位置）
                        // 需要报参数类型不匹配的错误。
                        dimension = -1;
                    } else if (symbol instanceof VarSymbol varSymbol) {
                        /*
                        举例说明：
                        一维数组名为实参，如a[2], foo(a)，则此时funcParam将其识别为0维的左值
                        从符号表查询得到a是1维，相减得这里的实参维数是1
                         */
                        dimension = varSymbol.getDimension() - paramSymbol.getDimension();
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
                    if (funcSymbol.getFuncParams().get(i).getType() != paramSymbol.getType()) {
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
