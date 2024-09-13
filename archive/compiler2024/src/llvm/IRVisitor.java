package llvm;

import frontend.Token;
import frontend.TokenType;
import llvm.types.ArrayType;
import llvm.types.FunctionType;
import llvm.types.IntType;
import llvm.types.PointerType;
import llvm.types.ValueType;
import llvm.types.VoidType;
import llvm.values.Argument;
import llvm.values.Assignable;
import llvm.values.BasicBlock;
import llvm.values.ConstArray;
import llvm.values.ConstInt;
import llvm.values.Function;
import llvm.values.Builder;
import llvm.values.GlobalVar;
import llvm.values.Value;
import llvm.values.instructions.BinaryInstruction;
import llvm.values.instructions.Operator;
import syntax.Block;
import syntax.BlockItem;
import syntax.CompUnit;
import syntax.ConstDecl;
import syntax.ConstDef;
import syntax.ConstInitVal;
import syntax.Decl;
import syntax.FuncDef;
import syntax.FuncFParam;
import syntax.InitVal;
import syntax.LVal;
import syntax.MainFuncDef;
import syntax.Number;
import syntax.VarDecl;
import syntax.VarDef;
import syntax.expression.AddExp;
import syntax.expression.Cond;
import syntax.expression.ConstExp;
import syntax.expression.EqExp;
import syntax.expression.Exp;
import syntax.expression.LAndExp;
import syntax.expression.LOrExp;
import syntax.expression.MulExp;
import syntax.expression.PrimaryExp;
import syntax.expression.RelExp;
import syntax.expression.UnaryExp;
import syntax.statement.BlockStmt;
import syntax.statement.BreakStmt;
import syntax.statement.ContinueStmt;
import syntax.statement.ExpStmt;
import syntax.statement.ForStmt;
import syntax.statement.ForStruct;
import syntax.statement.GetintStmt;
import syntax.statement.IfStmt;
import syntax.statement.LValExpStmt;
import syntax.statement.PrintfStmt;
import syntax.statement.ReturnStmt;
import syntax.statement.Stmt;

import java.util.ArrayList;
import java.util.Stack;

public class IRVisitor {
    private final CompUnit compUnit;
    private final SymbolTable symbolTable = new SymbolTable();
    private Function currentFunction;
    private BasicBlock currentBasicBlock;
    private BasicBlock currentTrueBlock;
    private BasicBlock currentFalseBlock;
    private BasicBlock currentEndBlock;
    private BasicBlock currentForEndBlock;
    private int immediate;
    private Value tempValue;
    private ValueType tempType;
    private boolean isGlobal;
    private boolean isCalculable;

    public IRVisitor(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void visitCompUnit() {
        symbolTable.addTable();
        symbolTable.addSymbol("getint",
                Builder.buildBulitInFunction("getint", new IntType(32), new ArrayList<>()));
        symbolTable.addSymbol("putint",
                Builder.buildBulitInFunction("putint", new VoidType(), new IntType(32)));
        symbolTable.addSymbol("putch",
                Builder.buildBulitInFunction("putch", new VoidType(), new IntType(32)));
        symbolTable.addSymbol("putstr",
                Builder.buildBulitInFunction("putstr", new VoidType(), new PointerType(new IntType(8))));
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
        if (decl instanceof ConstDecl constDecl) {
            visitConstDecl(constDecl);
        } else if (decl instanceof VarDecl varDecl) {
            visitVarDecl(varDecl);
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            tempType = new IntType(32);
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDef constDef) {
        String name = constDef.getIdent().getValue();
        isCalculable = true;
        if (constDef.getConstExps().isEmpty()) {
            visitConstInitVal(constDef.getConstInitVal());
            symbolTable.addConst(name, immediate);
            Value value = Builder.buildConstInt(immediate);
            if (isGlobal) {
                tempValue = Builder.buildGlobalVar(name, tempType, true, value);
            } else {
                tempValue = Builder.buildVar(tempType, value, currentBasicBlock);
            }
        } else {
            Stack<Integer> dimensions = new Stack<>();
            for (ConstExp constExp : constDef.getConstExps()) {
                visitConstExp(constExp);
                dimensions.push(immediate);
            }
            tempType = new ArrayType(new IntType(32), dimensions.pop());
            while (!dimensions.isEmpty()) {
                tempType = new ArrayType(tempType, dimensions.pop());
            }
            visitConstInitVal(constDef.getConstInitVal());
            if (isGlobal) {
                tempValue = Builder.buildGlobalArray(name, tempType, true, tempValue);
            } else {
                tempValue = Builder.buildArray(tempType, tempValue, currentBasicBlock);
            }
        }
        isCalculable = false;
        symbolTable.addSymbol(name, tempValue);
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() == null) {
            ConstArray constArray = new ConstArray();
            for (ConstInitVal val : constInitVal.getConstInitVals()) {
                visitConstInitVal(val);
                constArray.addValue(tempValue);
            }
            constArray.resetType();
            tempValue = constArray;
        } else {
            visitConstExp(constInitVal.getConstExp());
            if (isGlobal || isCalculable) {
                tempValue = Builder.buildConstInt(immediate);
            }
        }
    }

    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            tempType = new IntType(32);
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDef varDef) {
        String name = varDef.getIdent().getValue();
        if (varDef.getConstExps().isEmpty()) {
            if (varDef.getInitVal() == null) {
                immediate = 0;
            } else {
                visitInitVal(varDef.getInitVal());
            }
            if (isGlobal) {
                Value value = Builder.buildConstInt(immediate);
                tempValue = Builder.buildGlobalVar(name, tempType, false, value);
            } else if (varDef.getInitVal() != null) {
                tempValue = Builder.buildVar(tempType, tempValue, currentBasicBlock);
            } else {
                tempValue = Builder.buildVar(tempType, null, currentBasicBlock);
            }
        } else {
            Stack<Integer> dimensions = new Stack<>();
            isCalculable = true;
            for (ConstExp constExp : varDef.getConstExps()) {
                visitConstExp(constExp);
                dimensions.push(immediate);
            }
            isCalculable = false;
            tempValue = null;
            tempType = new ArrayType(new IntType(32), dimensions.pop());
            while (!dimensions.isEmpty()) {
                tempType = new ArrayType(tempType, dimensions.pop());
            }
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal());
            }
            if (isGlobal) {
                tempValue = Builder.buildGlobalArray(name, tempType, false, tempValue);
            } else {
                tempValue = Builder.buildArray(tempType, tempValue, currentBasicBlock);
            }
        }
        symbolTable.addSymbol(name, tempValue);
    }

    private void visitInitVal(InitVal initVal) {
        if (initVal.getExp() == null) {
            ConstArray constArray = new ConstArray();
            for (InitVal val : initVal.getInitVals()) {
                visitInitVal(val);
                if (isGlobal && val.getExp() != null) {
                    tempValue = Builder.buildConstInt(immediate);
                }
                constArray.addValue(tempValue);
            }
            constArray.resetType();
            tempValue = constArray;
        } else {
            visitExp(initVal.getExp());
        }
    }

    private void visitFuncDef(FuncDef funcDef) {
        String name = funcDef.getIdent().getValue();
        ArrayList<ValueType> argumentsType = new ArrayList<>();
        ValueType returnType;
        if (funcDef.getFuncType().getType() == TokenType.VOIDTK) {
            returnType = new VoidType();
        } else {
            returnType = new IntType(32);
        }
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getParams()) {
                argumentsType.add(getFParamType(funcFParam));
            }
        }
        FunctionType functionType = Builder.buildFunctionType(returnType, argumentsType);
        Function function = Builder.buildFunction(name, functionType);
        currentFunction = function;
        symbolTable.addSymbol(name, function);
        symbolTable.addTable();
        currentBasicBlock = Builder.buildBasicBlock(function);
        if (funcDef.getFuncFParams() != null) {
            int cnt = 0;
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getParams()) {
                visitFuncFParam(funcFParam, function.getArgument(cnt));
                cnt++;
            }
        }
        visitBlock(funcDef.getBlock());
        symbolTable.removeTable();
    }

    private ValueType getFParamType(FuncFParam funcFParam) {
        if (!funcFParam.isArray()) {
            return new IntType(32);
        } else {
            isCalculable = true;
            ValueType type = new IntType(32);
            for (ConstExp constExp : funcFParam.getConstExps()) {
                visitConstExp(constExp);
                type = new ArrayType(type, immediate);
            }
            isCalculable = false;
            return new PointerType(type);
        }
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        Function function = Builder.buildFunction("main",
                Builder.buildFunctionType(new IntType(32), new ArrayList<>()));
        currentFunction = function;
        symbolTable.addSymbol("main", function);
        symbolTable.addTable();
        currentBasicBlock = Builder.buildBasicBlock(currentFunction);
        visitBlock(mainFuncDef.getBlock());
        symbolTable.removeTable();
    }

    private void visitFuncFParam(FuncFParam funcFParam, Argument argument) {
        String name = funcFParam.getIdent().getValue();
        symbolTable.addSymbol(name, Builder.buildVar(argument.getType(),
                argument, currentBasicBlock));
    }

    private void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }

    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem.getDecl() != null) {
            visitDecl(blockItem.getDecl());
        } else if (blockItem.getStmt() != null) {
            visitStmt(blockItem.getStmt());
        }
    }

    private void visitStmt(Stmt stmt) {
        if (stmt instanceof IfStmt ifStmt) {
            visitIfStmt(ifStmt);
        } else if (stmt instanceof ForStruct forStruct) {
            visitForStruct(forStruct);
        } else if (stmt instanceof BreakStmt breakStmt) {
            visitBreakStmt(breakStmt);
        } else if (stmt instanceof ContinueStmt continueStmt) {
            visitContinueStmt(continueStmt);
        } else if (stmt instanceof BlockStmt blockStmt) {
            symbolTable.addTable();
            visitBlock(blockStmt.getBlock());
            symbolTable.removeTable();
        } else if (stmt instanceof LValExpStmt lValExpStmt) {
            doLValAssign(lValExpStmt.getlVal(), lValExpStmt.getExp());
        } else if (stmt instanceof GetintStmt getintStmt) {
            visitGetintStmt(getintStmt);
        } else if (stmt instanceof PrintfStmt printStmt) {
            visitPrintfStmt(printStmt);
        } else if (stmt instanceof ExpStmt expStmt) {
            visitExp(expStmt.getExp());
        } else if (stmt instanceof ReturnStmt returnStmt) {
            if (returnStmt.getExp() == null) {
                Builder.buildRetInstruction(currentBasicBlock);
            } else {
                visitExp(returnStmt.getExp());
                Builder.buildRetInstruction(currentBasicBlock, tempValue);
            }
        }
    }

    private void visitIfStmt(IfStmt ifStmt) {
        BasicBlock tempIfBlock = currentTrueBlock;
        BasicBlock tempElseBlock = currentFalseBlock;
        BasicBlock trueBlock = Builder.buildUnnamedBasicBlock();
        BasicBlock falseBlock = Builder.buildUnnamedBasicBlock();
        if (ifStmt.getStmt2() == null) {
            currentTrueBlock = trueBlock;
            currentFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());

            trueBlock.fillIntoFunction(currentFunction);
            currentBasicBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            Builder.buildBrInstruction(currentBasicBlock, falseBlock);

            falseBlock.fillIntoFunction(currentFunction);
            currentBasicBlock = falseBlock;
        } else {
            BasicBlock endBlock = Builder.buildUnnamedBasicBlock();
            currentTrueBlock = trueBlock;
            currentFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());

            trueBlock.fillIntoFunction(currentFunction);
            currentBasicBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            Builder.buildBrInstruction(currentBasicBlock, endBlock);

            falseBlock.fillIntoFunction(currentFunction);
            currentBasicBlock = falseBlock;
            visitStmt(ifStmt.getStmt2());
            Builder.buildBrInstruction(currentBasicBlock, endBlock);

            endBlock.fillIntoFunction(currentFunction);
            currentBasicBlock = endBlock;
        }
        currentTrueBlock = tempIfBlock;
        currentFalseBlock = tempElseBlock;
    }

    private void visitForStmt(ForStmt forStmt) {
        if (forStmt != null) {
            doLValAssign(forStmt.getlVal(), forStmt.getExp());
        }
    }

    private void visitForStruct(ForStruct forStruct) {
        BasicBlock tempTrueBlock = currentTrueBlock;
        BasicBlock tempFalseBlock = currentFalseBlock;
        visitForStmt(forStruct.getForStmt1());

        BasicBlock condBlock = Builder.buildBasicBlock(currentFunction);
        BasicBlock endBlock = Builder.buildUnnamedBasicBlock();
        BasicBlock trueBlock = Builder.buildUnnamedBasicBlock();
        BasicBlock falseBlock = Builder.buildUnnamedBasicBlock();
        Builder.buildBrInstruction(currentBasicBlock, condBlock);

        currentBasicBlock = condBlock;
        currentTrueBlock = trueBlock;
        currentFalseBlock = falseBlock;
        currentEndBlock = endBlock;
        currentForEndBlock = falseBlock;
        if (forStruct.getCond() == null) {
            Builder.buildBrInstruction(currentBasicBlock, trueBlock);
        } else {
            visitCond(forStruct.getCond());
        }

        trueBlock.fillIntoFunction(currentFunction);
        currentBasicBlock = trueBlock;
        visitStmt(forStruct.getStmt());
        Builder.buildBrInstruction(currentBasicBlock, endBlock);

        endBlock.fillIntoFunction(currentFunction);
        currentBasicBlock = endBlock;
        visitForStmt(forStruct.getForStmt2());
        Builder.buildBrInstruction(currentBasicBlock, condBlock);

        falseBlock.fillIntoFunction(currentFunction);
        currentBasicBlock = falseBlock;
        currentTrueBlock = tempTrueBlock;
        currentFalseBlock = tempFalseBlock;
    }

    private void visitBreakStmt(BreakStmt breakStmt) {
        Builder.buildBrInstruction(currentBasicBlock, currentForEndBlock);
    }

    private void visitContinueStmt(ContinueStmt continueStmt) {
        Builder.buildBrInstruction(currentBasicBlock, currentEndBlock);
    }

    private void doLValAssign(LVal lVal, Exp exp) {
        visitExp(exp);
        Value value = tempValue;
        assignLVal(lVal, value);
    }

    private void visitGetintStmt(GetintStmt getintStmt) {
        LVal lVal = getintStmt.getlVal();
        Function function = (Function) symbolTable.getValue("getint");
        Value call = Builder.buildCallInstruction(currentBasicBlock, function, new ArrayList<>());
        assignLVal(lVal, call);
    }

    private void assignLVal(LVal lVal, Value value) {
        Value pointer = symbolTable.getValue(lVal.getIdent().getValue());
        if (!lVal.getExps().isEmpty()) {
            ValueType valueType = pointer.getType();
            ValueType targetType = ((PointerType) valueType).getPointToType();
            ArrayList<Value> indexes = new ArrayList<>();
            if (targetType instanceof PointerType) {
                pointer = Builder.buildLoadInstruction(currentBasicBlock, pointer);
            } else {
                indexes.add(new ConstInt(0));
            }
            for (Exp exp : lVal.getExps()) {
                visitExp(exp);
                indexes.add(tempValue);
            }
            pointer = Builder.buildGEPInstruction(pointer, indexes, currentBasicBlock);
        }
        tempValue = Builder.buildStoreInstruction(currentBasicBlock, value, pointer);
    }

    private void visitPrintfStmt(PrintfStmt printfStmt) {
        Function putch = (Function) symbolTable.getValue("putch");
        Function putint = (Function) symbolTable.getValue("putint");
        String format = printfStmt.getFormat().getValue();
        int cnt = 0;
        for (int i = 1; i < format.length() - 1; i++) {
            char c = format.charAt(i);
            ArrayList<Value> values = new ArrayList<>();
            if (c == '%') {
                visitExp(printfStmt.getExps().get(cnt));
                cnt++;
                values.add(tempValue);
                i++;
                tempValue = Builder.buildCallInstruction(currentBasicBlock, putint, values);
            } else if (c == '\\') {
                values.add(Builder.buildConstInt('\n'));
                i++;
                tempValue = Builder.buildCallInstruction(currentBasicBlock, putch, values);
            } else {
                values.add(Builder.buildConstInt(c));
                tempValue = Builder.buildCallInstruction(currentBasicBlock, putch, values);
            }
        }
    }

    private void visitExp(Exp exp) {
        if (exp != null) {
            visitAddExp(exp.getAddExp());
        }
    }

    private void visitCond(Cond cond) {
        visitLOrExp(cond.getlOrExp());
    }

    private void visitLVal(LVal lVal) {
        String name = lVal.getIdent().getValue();
        Value pointer = symbolTable.getValue(name);

        if (isGlobal || isCalculable) {
            if (lVal.getExps().isEmpty()) {
                if (pointer instanceof GlobalVar globalVar) {
                    immediate = ((ConstInt) globalVar.getValue()).getValue();
                } else {
                    immediate = symbolTable.getConst(name);
                }
            } else {
                tempValue = ((GlobalVar) pointer).getValue();
                for (Exp exp : lVal.getExps()) {
                    visitExp(exp);
                    tempValue = ((ConstArray) tempValue).getValues().get(immediate);
                }
                immediate = ((ConstInt) tempValue).getValue();
            }
        } else {
            ArrayList<Value> indexes = new ArrayList<>();
            if (lVal.getExps().isEmpty()) {
                if (((PointerType) pointer.getType()).getPointToType() instanceof ArrayType) {
                    indexes.add(new ConstInt(0));
                    indexes.add(new ConstInt(0));
                    tempValue = Builder.buildGEPInstruction(pointer, indexes, currentBasicBlock);
                } else {
                    tempValue = Builder.buildLoadInstruction(currentBasicBlock, pointer);
                }
            } else {
                ValueType type = pointer.getType();
                ValueType targetType = ((PointerType) type).getPointToType();
                if (targetType instanceof PointerType) {
                    pointer = Builder.buildLoadInstruction(currentBasicBlock, pointer);
                } else {
                    indexes.add(new ConstInt(0));
                }
                for (Exp exp : lVal.getExps()) {
                    visitExp(exp);
                    indexes.add(tempValue);
                }
                pointer = Builder.buildGEPInstruction(pointer, indexes, currentBasicBlock);
                if (((PointerType) pointer.getType()).getPointToType() instanceof ArrayType) {
                    ArrayList<Value> indexes1 = new ArrayList<>();
                    indexes1.add(new ConstInt(0));
                    indexes1.add(new ConstInt(0));
                    tempValue = Builder.buildGEPInstruction(pointer, indexes1, currentBasicBlock);
                } else {
                    tempValue = Builder.buildLoadInstruction(currentBasicBlock, pointer);
                }
            }
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp());
        } else if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else if (primaryExp.getlVal() != null) {
            visitLVal(primaryExp.getlVal());
        }
    }

    private void visitNumber(Number number) {
        if (isGlobal || isCalculable) {
            immediate = number.getValue();
        } else {
            tempValue = Builder.buildConstInt(number.getValue());
        }
    }

    private void visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryExp() != null) {
            visitUnaryExp(unaryExp.getUnaryExp());
            TokenType op = unaryExp.getUnaryOp().getToken().getType();
            if (op == TokenType.MINU) {
                if (isGlobal || isCalculable) {
                    immediate = -immediate;
                } else {
                    tempValue = Builder.buildBinaryInstruction(currentBasicBlock, Operator.SUB,
                            new ConstInt(0), tempValue);
                }
            } else if (op == TokenType.NOT) {
                tempValue = Builder.buildBinaryInstruction(currentBasicBlock, Operator.ICMP_EQ,
                        new ConstInt(0), tempValue);
            }
        } else {
            String name = unaryExp.getIdent().getValue();
            Function function = (Function) symbolTable.getValue(name);
            ArrayList<Value> arguments = new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                for (Exp exp : unaryExp.getFuncRParams().getExps()) {
                    visitExp(exp);
                    arguments.add(tempValue);
                }
            }
            tempValue = Builder.buildCallInstruction(currentBasicBlock, function, arguments);
        }
    }

    private void visitMulExp(MulExp mulExp) {
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExps();
        ArrayList<Token> ops = mulExp.getOps();
        visitUnaryExp(unaryExps.get(0));
        Operator op = null;
        for (int i = 1; i < unaryExps.size(); i++) {
            Value prev = tempValue;
            int keep = immediate;
            visitUnaryExp(unaryExps.get(i));
            if (isGlobal || isCalculable) {
                immediate = calculate(keep, immediate, mulExp.getOps().get(i - 1).getType());
            } else {
                switch (ops.get(i - 1).getType()) {
                    case MULT -> op = Operator.MUL;
                    case DIV -> op = Operator.SDIV;
                    case MOD -> op = Operator.SREM;
                }
                tempValue = Builder.buildBinaryInstruction(currentBasicBlock, op, prev, tempValue);
            }
        }
    }

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

    private void visitAddExp(AddExp addExp) {
        ArrayList<MulExp> mulExps = addExp.getMulExps();
        visitMulExp(mulExps.get(0));
        for (int i = 1; i < mulExps.size(); i++) {
            int keep = immediate;
            Value prev = tempValue;
            visitMulExp(mulExps.get(i));
            if (isGlobal || isCalculable) {
                immediate = calculate(keep, immediate, addExp.getOps().get(i - 1).getType());
            } else {
                Operator op = null;
                switch (addExp.getOps().get(i - 1).getType()) {
                    case PLUS -> op = Operator.ADD;
                    case MINU -> op = Operator.SUB;
                }
                tempValue = Builder.buildBinaryInstruction(currentBasicBlock, op, prev, tempValue);
            }
        }
    }

    private void visitRelExp(RelExp relExp) {
        visitAddExp(relExp.getAddExps().get(0));
        for (int i = 1; i < relExp.getAddExps().size(); i++) {
            Value prev = tempValue;
            visitAddExp(relExp.getAddExps().get(i));
            Operator op = null;
            switch (relExp.getOps().get(i - 1).getType()) {
                case LSS -> op = Operator.ICMP_SLT;
                case LEQ -> op = Operator.ICMP_SLE;
                case GRE -> op = Operator.ICMP_SGT;
                case GEQ -> op = Operator.ICMP_SGE;
            }
            tempValue = Builder.buildBinaryInstruction(currentBasicBlock, op, prev, tempValue);
        }
    }

    private void visitEqExp(EqExp eqExp) {
        visitRelExp(eqExp.getRelExps().get(0));
        if (eqExp.getRelExps().size() == 1 && tempValue instanceof Assignable) {
            if (!(tempValue instanceof BinaryInstruction binaryInst && binaryInst.isLogical())) {
                tempValue = Builder.buildBinaryInstruction(currentBasicBlock, Operator.ICMP_NE,
                        tempValue, new ConstInt(0));
            }
            return;
        }
        for (int i = 1; i < eqExp.getRelExps().size(); i++) {
            Value prev = tempValue;
            visitRelExp(eqExp.getRelExps().get(i));
            Operator op = null;
            switch (eqExp.getOps().get(i - 1).getType()) {
                case EQL -> op = Operator.ICMP_EQ;
                case NEQ -> op = Operator.ICMP_NE;
            }
            tempValue = Builder.buildBinaryInstruction(currentBasicBlock, op, prev, tempValue);
        }
    }

    private void visitLAndExp(LAndExp lAndExp) {
        BasicBlock trueBlock = currentTrueBlock;
        BasicBlock falseBlock = currentFalseBlock;
        int len = lAndExp.getEqExps().size();
        for (int i = 0; i < len - 1; i++) {
            visitEqExp(lAndExp.getEqExps().get(i));
            BasicBlock thenBlock = Builder.buildBasicBlock(currentFunction);
            Builder.buildBrInstruction(currentBasicBlock, thenBlock, currentFalseBlock, tempValue);
            currentBasicBlock = thenBlock;
        }
        visitEqExp(lAndExp.getEqExps().get(len - 1));
        Builder.buildBrInstruction(currentBasicBlock, trueBlock, falseBlock, tempValue);
        currentTrueBlock = trueBlock;
        currentFalseBlock = falseBlock;
    }

    private void visitLOrExp(LOrExp lOrExp) {
        BasicBlock trueBlock = currentTrueBlock;
        BasicBlock falseBlock = currentFalseBlock;
        int len = lOrExp.getlAndExps().size();
        for (int i = 0; i < len - 1; i++) {
            BasicBlock thenBlock = Builder.buildUnnamedBasicBlock();
            currentFalseBlock = thenBlock;
            visitLAndExp(lOrExp.getlAndExps().get(i));
            thenBlock.fillIntoFunction(currentFunction);
            currentBasicBlock = thenBlock;
        }
        currentTrueBlock = trueBlock;
        currentFalseBlock = falseBlock;
        visitLAndExp(lOrExp.getlAndExps().get(len - 1));
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }
}
