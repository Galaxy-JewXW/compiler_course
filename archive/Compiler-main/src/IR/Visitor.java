package IR;

import IR.SymbolTable.SymbolTable;
import IR.types.*;
import IR.values.*;
import IR.values.instructions.BinaryInst;
import IR.values.ConstArray;
import IR.values.instructions.Operator;
import config.Config;
import node.*;
import node.Number;
import node.decl.*;
import node.expression.*;
import node.func.FuncDef;
import node.func.FuncFParam;
import node.func.MainFuncDef;
import node.stmt.*;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class Visitor {
    private final SymbolTable symbolTables = new SymbolTable();
    private final BuildFactory buildFactory = BuildFactory.getInstance();

    /*信号*/
    private Function curFunction;
    private BasicBlock curBlock;
    private BasicBlock curTrueBlock;
    private BasicBlock curFalseBlock;
    private BasicBlock curEndBlock;
    private BasicBlock curForEndBlock;
    private int immediate;
    private Value tmpValue;
    private Type tmpType;
    private boolean isGlobal;
    private boolean calculable;

    private int calculate(int a, int b, TokenType op) {
        switch (op) {
            case PLUS -> {
                return a + b;
            }
            case MINU -> {
                return a - b;
            }
            case MULT -> {
                return a * b;
            }
            case DIV -> {
                return a / b;
            }
            case MOD -> {
                return a % b;
            }
            default -> {
                return 0;
            }
        }
    }

    public void visitCompUnit(CompUnit compUnit) {
        symbolTables.addSymbolTable();
        symbolTables.addSymbol("getint", buildFactory.buildLibFunc("getint", IntegerType.i32, new ArrayList<>()));
        symbolTables.addSymbol("putint", buildFactory.buildOneParamLibFunc("putint", VoidType.voidType, IntegerType.i32));
        symbolTables.addSymbol("putch", buildFactory.buildOneParamLibFunc("putch", VoidType.voidType, IntegerType.i32));
        symbolTables.addSymbol("putstr", buildFactory.buildOneParamLibFunc("putstr", VoidType.voidType, new PointerType(IntegerType.i8)));
        for (Decl decl : compUnit.getDecls()) {
            isGlobal = true;
            visitDecl(decl);
            isGlobal = false;
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(compUnit.getMainFuncDef());
    }

    private void visitDecl(Decl decl) {
        if (decl instanceof ConstDecl) {
            visitConstDecl((ConstDecl) decl);
        } else {
            visitVarDecl((VarDecl) decl);
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            tmpType = IntegerType.i32;//int
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDef constDef) {
        String name = constDef.getIdent().getToken();
        calculable = true;
        if (!constDef.getConstExps().isEmpty()) {//isArray
            Stack<Integer> dims = new Stack<>();
            for (ConstExp constExp : constDef.getConstExps()) {
                visitConstExp(constExp);
                dims.push(immediate);
            }
            tmpType = new ArrayType(IntegerType.i32, dims.pop());
            while (!dims.empty()) {
                tmpType = new ArrayType(tmpType, dims.pop());
            }
            visitConstInitVal(constDef.getConstInitVal());
            if (isGlobal) {
                tmpValue = buildFactory.buildGlobalArray(name, tmpType, true, tmpValue);
            } else {
                tmpValue = buildFactory.buildArray(tmpType, tmpValue, curBlock);
            }
        } else {
            visitConstInitVal(constDef.getConstInitVal());
            symbolTables.addConst(name, immediate);
            Value value = buildFactory.buildConstInt(immediate);
            if (isGlobal) {
                tmpValue = buildFactory.globalVar(name, tmpType, true, value);
            } else {
                tmpValue = buildFactory.buildVar(tmpType, value, curBlock);
            }
        }
        calculable = false;
        symbolTables.addSymbol(name, tmpValue);
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() != null) {
            visitConstExp(constInitVal.getConstExp());
            if (isGlobal || calculable) {
                tmpValue = new ConstInt(immediate);
            }
        } else {
            ConstArray res = new ConstArray();
            for (ConstInitVal constInitVal1 : constInitVal.getConstInitVals()) {
                visitConstInitVal(constInitVal1);
                res.addVal(tmpValue);
            }
            res.resetType();
            tmpValue = res;
        }
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }


    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            tmpType = IntegerType.i32;
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDef varDef) {
        String name = varDef.getIdent().getToken();
        if (varDef.getConstExps().isEmpty()) {
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal());
            } else {
                immediate = 0;
            }
            if (isGlobal) {
                Value value = buildFactory.buildConstInt(immediate);
                tmpValue = buildFactory.globalVar(name, tmpType, false, value);
            } else {
                if (varDef.getInitVal() != null) {
                    tmpValue = buildFactory.buildVar(tmpType, tmpValue, curBlock);
                } else {
                    tmpValue = buildFactory.buildVar(tmpType, null, curBlock);
                }
            }
        } else {
            Stack<Integer> dims = new Stack<>();
            calculable = true;
            for (ConstExp constExp : varDef.getConstExps()) {
                visitConstExp(constExp);
                dims.push(immediate);
            }
            calculable = false;
            tmpType = new ArrayType(IntegerType.i32, dims.pop());
            while (!dims.empty()) {
                tmpType = new ArrayType(tmpType, dims.pop());
            }
            tmpValue = null;
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal());
            }
            if (isGlobal) {
                tmpValue = buildFactory.buildGlobalArray(name, tmpType, false, tmpValue);
            } else {
                tmpValue = buildFactory.buildArray(tmpType, tmpValue, curBlock);
            }
        }
        symbolTables.addSymbol(name, tmpValue);
    }

    private void visitInitVal(InitVal initVal) {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp());
        } else {
            ConstArray res = new ConstArray();
            for (InitVal initVal1 : initVal.getInitVals()) {
                visitInitVal(initVal1);
                if (isGlobal && initVal1.getExp() != null) {
                    tmpValue = new ConstInt(immediate);
                }
                res.addVal(tmpValue);
            }
            res.resetType();
            tmpValue = res;
        }
    }

    private void visitFuncDef(FuncDef funcDef) {
        String name = funcDef.getIdent().getToken();
        ArrayList<Type> argTypes = new ArrayList<>();
        Type retType;
        if (funcDef.getFuncType().getFuncType().getSymbol() == TokenType.VOIDTK) {
            retType = VoidType.voidType;
        } else {
            retType = IntegerType.i32;
        }
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                argTypes.add(getFuncFParamType(funcFParam));
            }
        }
        FuncType funcType = new FuncType(retType, argTypes);
        Function function = buildFactory.buildFunction(name, funcType);
        curFunction = function;
        symbolTables.addSymbol(name, function);
        symbolTables.addSymbolTable();
        curBlock = buildFactory.buildBasicBlock(function);
        if (funcDef.getFuncFParams() != null) {
            int index = 0;
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                visitFuncFParam(funcFParam, function.getKArg(index++));
            }
        }
        visitBlock(funcDef.getBlock());
        symbolTables.rmSymbolTable();
    }

    private Type getFuncFParamType(FuncFParam funcFParam) {
        if (!funcFParam.isArray()) {
            return IntegerType.i32;
        } else {
            calculable = true;
            Type type = IntegerType.i32;
            for (ConstExp constExp : funcFParam.getConstExps()) {
                visitConstExp(constExp);
                type = new ArrayType(type, immediate);
            }
            calculable = false;
            return new PointerType(type);
        }
    }

    private void visitFuncFParam(FuncFParam funcFParam, Argument arg) {
        String name = funcFParam.getIdent().getToken();
        Value value = buildFactory.buildVar(arg.getType(), arg, curBlock);
        symbolTables.addSymbol(name, value);
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        FuncType funcType = buildFactory.buildFuncType(new IntegerType(32), new ArrayList<>());
        Function function = buildFactory.buildFunction("main", funcType);
        curFunction = function;
        symbolTables.addSymbol("main", function);
        symbolTables.addSymbolTable();
        curBlock = buildFactory.buildBasicBlock(curFunction);
        visitBlock(mainFuncDef.getBlock());
        symbolTables.rmSymbolTable();
    }

    private void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }

    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem.getDecl() != null) {
            visitDecl(blockItem.getDecl());
        } else {
            visitStmt(blockItem.getStmt());
        }
    }

    private void visitStmt(Stmt stmt) {
        if (stmt instanceof ReturnStmt returnStmt) {
            if (returnStmt.getExp() != null) {
                visitExp(returnStmt.getExp());
                buildFactory.buildRetInst(curBlock, tmpValue);
            } else {
                buildFactory.buildRetInst(curBlock);
            }
        } else if (stmt instanceof BlockStmt blockStmt) {
            symbolTables.addSymbolTable();
            visitBlock(blockStmt.getBlock());
            symbolTables.rmSymbolTable();
        } else if (stmt instanceof AssignExpStmt assignExpStmt) {
            doLValAssignExp(assignExpStmt.getlVal(), assignExpStmt.getExp());
        } else if (stmt instanceof AssignGetintStmt assignGetintStmt) {
            doGetInt(assignGetintStmt);
        } else if (stmt instanceof PrintfStmt printfStmt) {
            if (Config.char2str) {
                optimizePrintf(printfStmt);
            } else {
                doPrintf(printfStmt);
            }
        } else if (stmt instanceof ExpStmt expStmt) {
            visitExp(expStmt.getExp());
        } else if (stmt instanceof IfStmt ifStmt) {
            doIfStmt(ifStmt);
        } else if (stmt instanceof For stmtFor) {
            doFor(stmtFor);
        } else if (stmt instanceof BreakStmt) {
            doBreak();
        } else if (stmt instanceof ContinueStmt) {
            doContinue();
        }
    }

    private void visitForStmt(ForStmt forStmt) {
        if (forStmt == null) {
            return;
        }
        doLValAssignExp(forStmt.getlVal(), forStmt.getExp());
    }

    private void doLValAssignExp(LVal lVal, Exp exp) {
        visitExp(exp);
        Value ans = tmpValue;
        doLValAssign(lVal, ans);
    }

    private void doGetInt(AssignGetintStmt assignGetintStmt) {
        LVal lVal = assignGetintStmt.getlVal();
        Function function = (Function) symbolTables.getValue("getint");
        Value ans = buildFactory.callInst(curBlock, function, new ArrayList<>());
        doLValAssign(lVal, ans);
    }

    private void doLValAssign(LVal lVal, Value ans) {
        Value pointer = symbolTables.getValue(lVal.getIdent().getToken());
        if (!lVal.getExps().isEmpty()) {
            Type type = pointer.getType();
            Type target = ((PointerType) type).getTargetType();
            ArrayList<Value> indices = new ArrayList<>();
            if (target instanceof PointerType) {
                //参数是a[][1],a[0][1]=getint()这样的
                pointer = buildFactory.loadInst(curBlock, pointer);
            } else {
                indices.add(ConstInt.ZERO);
            }
            for (Exp exp : lVal.getExps()) {
                visitExp(exp);
                indices.add(tmpValue);
            }
            pointer = buildFactory.gepInst(pointer, indices, curBlock);
        }
        tmpValue = buildFactory.storeInst(curBlock, ans, pointer);
    }

    private void doContinue() {
        buildFactory.brInst(curBlock, curEndBlock);
    }

    private void doBreak() {
        buildFactory.brInst(curBlock, curForEndBlock);
    }

    private void doFor(For stmtFor) {
        BasicBlock tmpTrueBlock = curTrueBlock;
        BasicBlock tmpFalseBlock = curFalseBlock;
        visitForStmt(stmtFor.getForStmt1());//forStmt1
        BasicBlock condBlock = buildFactory.buildBasicBlock(curFunction);//cond
        BasicBlock endBlock = buildFactory.unnamedBasicBlock();//forStmt2
        BasicBlock trueBlock = buildFactory.unnamedBasicBlock();//stmt
        BasicBlock falseBlock = buildFactory.unnamedBasicBlock();//后继块
        buildFactory.brInst(curBlock, condBlock);//没有新的变量引入，可以不重填
        //cond
        curBlock = condBlock;
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        curEndBlock = endBlock;
        curForEndBlock = falseBlock;
        if (stmtFor.getCond() != null) {
            visitCond(stmtFor.getCond());//cond内会处理跳转
        } else {
            buildFactory.brInst(curBlock, trueBlock);
        }
        //stmt
        trueBlock.refill(curFunction);
        curBlock = trueBlock;
        visitStmt(stmtFor.getStmt());
        buildFactory.brInst(curBlock, endBlock);
        //forStmt2
        endBlock.refill(curFunction);
        curBlock = endBlock;
        visitForStmt(stmtFor.getForStmt2());
        buildFactory.brInst(curBlock, condBlock);
        //跳到之后的basicBlock
        falseBlock.refill(curFunction);
        curBlock = falseBlock;
        curTrueBlock = tmpTrueBlock;
        curFalseBlock = tmpFalseBlock;
    }

    private void doIfStmt(IfStmt ifStmt) {
        BasicBlock tmpTrueBlock = curTrueBlock;
        BasicBlock tmpFalseBlock = curFalseBlock;
        if (ifStmt.getStmt2() == null) {
            BasicBlock trueBlock = buildFactory.unnamedBasicBlock();
            BasicBlock falseBlock = buildFactory.unnamedBasicBlock();
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());
            trueBlock.refill(curFunction);
            curBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            buildFactory.brInst(curBlock, falseBlock);
            falseBlock.refill(curFunction);
            curBlock = falseBlock;
        } else {
            BasicBlock trueBlock = buildFactory.unnamedBasicBlock();
            BasicBlock falseBlock = buildFactory.unnamedBasicBlock();
            BasicBlock endBlock = buildFactory.unnamedBasicBlock();
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());
            trueBlock.refill(curFunction);
            curBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            buildFactory.brInst(curBlock, endBlock);
            falseBlock.refill(curFunction);
            curBlock = falseBlock;
            visitStmt(ifStmt.getStmt2());
            buildFactory.brInst(curBlock, endBlock);
            endBlock.refill(curFunction);
            curBlock = endBlock;
        }
        curTrueBlock = tmpTrueBlock;
        curFalseBlock = tmpFalseBlock;
    }

    private void doPrintf(PrintfStmt printfStmt) {
        Function putCh = (Function) symbolTables.getValue("putch");
        Function putInt = (Function) symbolTables.getValue("putint");
        String format = printfStmt.getFormatString().getToken();
        int cnt = 0;
        for (int i = 1; i < format.length() - 1; i++) {
            char c = format.charAt(i);
            ArrayList<Value> args = new ArrayList<>();
            if (c == '%') {
                i++;//%d
                visitExp(printfStmt.getExps().get(cnt++));
                args.add(tmpValue);
                tmpValue = buildFactory.callInst(curBlock, putInt, args);
            } else if (c == '\\') {
                i++;// \n
                args.add(buildFactory.buildConstInt('\n'));
                tmpValue = buildFactory.callInst(curBlock, putCh, args);
            } else {
                args.add(buildFactory.buildConstInt(c));
                tmpValue = buildFactory.callInst(curBlock, putCh, args);
            }
        }
    }

    HashMap<String, Integer> strMap = new HashMap<>();
    int strCnt = 0;

    private int getStrIndex(String str) {
        if (strMap.containsKey(str)) {
            return strMap.get(str);
        }
        ConstStr constStr = new ConstStr(str);
        ArrayType arrayType = new ArrayType(IntegerType.i8, constStr.getLength());
        String strName = getStrName(strCnt);
        Value value = buildFactory.globalVar(strName, arrayType, true, constStr);
        symbolTables.addGlobalSymbol(strName, value);
        strMap.put(str, strCnt);
        return strCnt++;
    }

    private String getStrName(int index) {
        return "hs_new_global_str_" + index;
    }

    private String getStrName(String str) {
        return getStrName(getStrIndex(str));
    }

    private void optimizePrintf(PrintfStmt printfStmt) {
        Function putCh = (Function) symbolTables.getValue("putch");
        Function putStr = (Function) symbolTables.getValue("putstr");
        Function putInt = (Function) symbolTables.getValue("putint");
        String format = printfStmt.getFormatString().getToken();
        int cnt = 0;
        for (int i = 1; i < format.length() - 1; i++) {
            ArrayList<Value> args = new ArrayList<>();
            if (format.charAt(i) == '%') {
                i++;//%d
                visitExp(printfStmt.getExps().get(cnt++));
                args.add(tmpValue);
                tmpValue = buildFactory.callInst(curBlock, putInt, args);
                continue;
            }
            int j = i;
            while (j < format.length() - 1 && format.charAt(j) != '%') {
                j++;
            }
            String str = format.substring(i, j).replace("\\n", "\n");
            if (str.length() == 1) {
                args.add(buildFactory.buildConstInt(str.charAt(0)));
                tmpValue = buildFactory.callInst(curBlock, putCh, args);
            } else {
                Value globalStr = symbolTables.getValue(getStrName(str));
                ArrayList<Value> indices = new ArrayList<>();
                indices.add(ConstInt.ZERO);
                indices.add(ConstInt.ZERO);
                args.add(buildFactory.gepInst(globalStr, indices, curBlock));
                tmpValue = buildFactory.callInst(curBlock, putStr, args);
            }
            i = j - 1;
        }
    }

    private void visitCond(Cond cond) {
        visitLOrExp(cond.getlOrExp());
    }

    private void visitLOrExp(LOrExp lOrExp) {
        BasicBlock trueBlock = curTrueBlock;//类似压栈弹栈的操作
        BasicBlock falseBlock = curFalseBlock;
        int len = lOrExp.getlAndExps().size();
        for (int i = 0; i < len - 1; i++) {
            BasicBlock thenBlock = buildFactory.unnamedBasicBlock();
            curFalseBlock = thenBlock;
            visitLAndExp(lOrExp.getlAndExps().get(i));
            thenBlock.refill(curFunction);//暂定采用回填方式解决
            curBlock = thenBlock;
        }
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        visitLAndExp(lOrExp.getlAndExps().get(len - 1));
    }

    private void visitLAndExp(LAndExp lAndExp) {
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        int len = lAndExp.getEqExps().size();
        for (int i = 0; i < len - 1; i++) {
            visitEqExp(lAndExp.getEqExps().get(i));
            BasicBlock thenBlock = buildFactory.buildBasicBlock(curFunction);
            buildFactory.brInst(curBlock, thenBlock, curFalseBlock, tmpValue);
            curBlock = thenBlock;
        }
        visitEqExp(lAndExp.getEqExps().get(len - 1));
        buildFactory.brInst(curBlock, trueBlock, falseBlock, tmpValue);
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
    }

    private void visitEqExp(EqExp eqExp) {
        visitRelExp(eqExp.getRelExps().get(0));
        if (eqExp.getRelExps().size() == 1 && tmpValue instanceof Assignable) {
            if (!(tmpValue instanceof BinaryInst binaryInst && binaryInst.isLogical())) {
                tmpValue = buildFactory.binaryInst(curBlock, Operator.Ne, tmpValue, ConstInt.ZERO);
            }
            return;
        }
        for (int i = 1; i < eqExp.getRelExps().size(); i++) {
            Value prevAns = tmpValue;
            visitRelExp(eqExp.getRelExps().get(i));
            Operator op = null;
            switch (eqExp.getOps().get(i - 1).getSymbol()) {
                case EQL -> op = Operator.Eq;
                case NEQ -> op = Operator.Ne;
            }
            tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
        }
    }

    private void visitRelExp(RelExp relExp) {
        visitAddExp(relExp.getAddExps().get(0));
        for (int i = 1; i < relExp.getAddExps().size(); i++) {
            Value prevAns = tmpValue;
            visitAddExp(relExp.getAddExps().get(i));
            Operator op = null;
            switch (relExp.getOps().get(i - 1).getSymbol()) {
                case LSS -> op = Operator.Slt;//<
                case LEQ -> op = Operator.Sle;//<=
                case GRE -> op = Operator.Sgt;//>
                case GEQ -> op = Operator.Sge;//>=
            }
            tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
        }
    }


    private void visitExp(Exp exp) {
        if (exp == null) {
            return;
        }
        visitAddExp(exp.getAddExp());
    }

    private void visitAddExp(AddExp addExp) {
        ArrayList<MulExp> mulExps = addExp.getMulExps();
        visitMulExp(mulExps.get(0));
        for (int i = 1; i < mulExps.size(); i++) {
            int keep = immediate;
            Value prevAns = tmpValue;
            visitMulExp(mulExps.get(i));
            if (isGlobal || calculable) {
                immediate = calculate(keep, immediate, addExp.getOps().get(i - 1).getSymbol());
            } else {
                Operator op = null;
                switch (addExp.getOps().get(i - 1).getSymbol()) {
                    case PLUS -> op = Operator.Add;
                    case MINU -> op = Operator.Sub;
                }
                tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
            }
        }
    }

    private void visitMulExp(MulExp mulExp) {
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExps();
        ArrayList<Token> ops = mulExp.getOps();
        visitUnaryExp(unaryExps.get(0));
        Operator op = null;
        for (int i = 1; i < unaryExps.size(); i++) {
            Value prevAns = tmpValue;
            int keep = immediate;
            visitUnaryExp(unaryExps.get(i));
            if (isGlobal || calculable) {
                immediate = calculate(keep, immediate, mulExp.getOps().get(i - 1).getSymbol());
            } else {
                switch (ops.get(i - 1).getSymbol()) {
                    case MULT -> op = Operator.Mul;
                    case DIV -> op = Operator.SDiv;
                    case MOD -> op = Operator.Mod;
                }
                tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
            }
        }
    }

    private void visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryExp() != null) {
            visitUnaryExp(unaryExp.getUnaryExp());
            TokenType op = unaryExp.getUnaryOp().getOp().getSymbol();
            if (op == TokenType.MINU) {
                if (isGlobal || calculable) {
                    immediate = -immediate;
                } else {
                    tmpValue = buildFactory.binaryInst(curBlock, Operator.Sub, ConstInt.ZERO, tmpValue);
                }
            } else if (op == TokenType.NOT) {
                tmpValue = buildFactory.binaryInst(curBlock, Operator.Eq, ConstInt.ZERO, tmpValue);
            }
        } else {
            String name = unaryExp.getIdent().getToken();
            Function func = (Function) symbolTables.getValue(name);
            ArrayList<Value> args = new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                for (Exp exp : unaryExp.getFuncRParams().getExps()) {
                    visitExp(exp);
                    args.add(tmpValue);
                }
            }
            tmpValue = buildFactory.callInst(curBlock, func, args);
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp());
        } else if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else {
            visitLVal(primaryExp.getlVal());
        }
    }

    private void visitNumber(Number number) {
        if (isGlobal || calculable) {
            immediate = number.getVal();
        } else {
            tmpValue = new ConstInt(number.getVal());
        }
    }

    private void visitLVal(LVal lVal) {
        String name = lVal.getIdent().getToken();
        Value pointer = symbolTables.getValue(name);
        ArrayList<Value> indices = new ArrayList<>();
        if (isGlobal || calculable) {
            if (lVal.getExps().isEmpty()) {
                if (pointer instanceof GlobalVar) {
                    immediate = ((ConstInt) ((GlobalVar) pointer).getValue()).getVal();
                } else {
                    immediate = symbolTables.getConst(name);
                }
            } else {
                tmpValue = ((GlobalVar) pointer).getValue();
                for (Exp exp : lVal.getExps()) {
                    visitExp(exp);
                    tmpValue = ((ConstArray) tmpValue).getValues().get(immediate);
                }
                immediate = ((ConstInt) tmpValue).getVal();
            }
            return;
        }
        if (lVal.getExps().isEmpty()) {
            if (!(((PointerType) pointer.getType()).getTargetType() instanceof ArrayType)) {
                tmpValue = buildFactory.loadInst(curBlock, pointer);
            } else {
                indices.add(ConstInt.ZERO);
                indices.add(ConstInt.ZERO);
                tmpValue = buildFactory.gepInst(pointer, indices, curBlock);
            }
        } else {
            //a[i][j] or a[i]
            Type type = pointer.getType();
            Type targetType = ((PointerType) type).getTargetType();
            if (targetType instanceof PointerType) {
                pointer = buildFactory.loadInst(curBlock, pointer);
            } else {
                indices.add(ConstInt.ZERO);
            }
            for (Exp exp : lVal.getExps()) {
                visitExp(exp);
                indices.add(tmpValue);
            }
            pointer = buildFactory.gepInst(pointer, indices, curBlock);
            if (((PointerType) pointer.getType()).getTargetType() instanceof ArrayType) {
                ArrayList<Value> indices1 = new ArrayList<>();
                indices1.add(ConstInt.ZERO);
                indices1.add(ConstInt.ZERO);
                tmpValue = buildFactory.gepInst(pointer, indices1, curBlock);
            } else {
                tmpValue = buildFactory.loadInst(curBlock, pointer);
            }
        }
    }
}
