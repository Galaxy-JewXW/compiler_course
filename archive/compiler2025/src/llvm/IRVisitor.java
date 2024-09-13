package llvm;

import llvm.types.FuncType;
import syntax.Number;
import llvm.types.*;
import llvm.values.*;
import llvm.values.instructions.BinaryInst;
import llvm.values.instructions.Operator;
import syntax.*;
import syntax.expression.*;
import frontend.Token;
import frontend.TokenType;
import syntax.statement.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class IRVisitor {
    private final SymbolTableManager symbolTableManager = new SymbolTableManager();
    private final CompUnit compUnit;
    private Function curFunction;
    private BasicBlock curBlock;
    private BasicBlock curTrueBlock;
    private BasicBlock curFalseBlock;
    private BasicBlock curEndBlock;
    private BasicBlock curForEndBlock;
    private int immediate;
    private Value tempValue;
    private Type tempType;
    private boolean isGlobal;
    private boolean isCalculable;

    public IRVisitor(CompUnit root) {
        this.compUnit = root;
    }

    private int cal(int a, int b, TokenType op) {
        return switch (op) {
            case PLUS -> a + b;
            case MINU -> a - b;
            case MULT -> a * b;
            case DIV -> a / b;
            case MOD -> a % b;
            default -> 0;
        };
    }

    public void visitCompUnit() {
        symbolTableManager.addTable();
        symbolTableManager.addSymbol("getint", InstrBuilder.buildBuiltInFunc("getint",
                IntegerType.i32, new ArrayList<>()));
        symbolTableManager.addSymbol("putint", InstrBuilder.buildBuiltInFunc("putint",
                VoidType.voidType, IntegerType.i32));
        symbolTableManager.addSymbol("putch", InstrBuilder.buildBuiltInFunc("putch",
                VoidType.voidType, IntegerType.i32));
        symbolTableManager.addSymbol("putstr", InstrBuilder.buildBuiltInFunc("putstr",
                VoidType.voidType, new PointerType(IntegerType.i8)));
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
            tempType = IntegerType.i32;
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDef constDef) {
        String name = constDef.getIdent().getValue();
        isCalculable = true;
        if (!constDef.getConstExps().isEmpty()) {
            Stack<Integer> dimensions = new Stack<>();
            for (ConstExp constExp : constDef.getConstExps()) {
                visitConstExp(constExp);
                dimensions.push(immediate);
            }
            tempType = new ArrayType(IntegerType.i32, dimensions.pop());
            while (!dimensions.isEmpty()) {
                tempType = new ArrayType(tempType, dimensions.pop());
            }
            visitConstInitVal(constDef.getConstInitVal());
            tempValue = isGlobal ? InstrBuilder.buildGlobalArray(name, tempType, true, tempValue)
                    : InstrBuilder.buildArray(tempType, tempValue, curBlock);
        } else {
            visitConstInitVal(constDef.getConstInitVal());
            symbolTableManager.addConst(name, immediate);
            Value value = InstrBuilder.buildConstInt(immediate);
            tempValue = isGlobal ? InstrBuilder.buildGlobalVar(name, tempType, true, value)
                    : InstrBuilder.buildVar(tempType, value, curBlock);
        }
        isCalculable = false;
        symbolTableManager.addSymbol(name, tempValue);
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() != null) {
            visitConstExp(constInitVal.getConstExp());
            if (isGlobal || isCalculable) {
                tempValue = new ConstInt(immediate);
            }
        } else {
            ConstArray res = new ConstArray();
            for (ConstInitVal constInitVal1 : constInitVal.getConstInitVals()) {
                visitConstInitVal(constInitVal1);
                res.addVal(tempValue);
            }
            res.resetType();
            tempValue = res;
        }
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }

    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            tempType = IntegerType.i32;
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDef varDef) {
        String name = varDef.getIdent().getValue();
        if (varDef.getConstExps().isEmpty()) {
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal());
            } else {
                immediate = 0;
            }
            Value value = InstrBuilder.buildConstInt(immediate);
            tempValue = isGlobal ? InstrBuilder.buildGlobalVar(name, tempType, false, value)
                    : InstrBuilder.buildVar(tempType, varDef.getInitVal() != null ? tempValue : null, curBlock);
        } else {
            Stack<Integer> dimensions = new Stack<>();
            isCalculable = true;
            for (ConstExp constExp : varDef.getConstExps()) {
                visitConstExp(constExp);
                dimensions.push(immediate);
            }
            isCalculable = false;
            tempType = new ArrayType(IntegerType.i32, dimensions.pop());
            while (!dimensions.isEmpty()) {
                tempType = new ArrayType(tempType, dimensions.pop());
            }
            tempValue = varDef.getInitVal() != null ? visitInitVal(varDef.getInitVal()) : null;
            tempValue = isGlobal ? InstrBuilder.buildGlobalArray(name, tempType, false, tempValue)
                    : InstrBuilder.buildArray(tempType, tempValue, curBlock);
        }
        symbolTableManager.addSymbol(name, tempValue);
    }

    private Value visitInitVal(InitVal initVal) {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp());
        } else {
            ConstArray res = new ConstArray();
            for (InitVal initVal1 : initVal.getInitVals()) {
                visitInitVal(initVal1);
                if (isGlobal && initVal1.getExp() != null) {
                    tempValue = new ConstInt(immediate);
                }
                res.addVal(tempValue);
            }
            res.resetType();
            tempValue = res;
        }
        return tempValue;
    }

    private void visitFuncDef(FuncDef funcDef) {
        String name = funcDef.getIdent().getValue();
        ArrayList<Type> argTypes = new ArrayList<>();
        Type retType = funcDef.getFuncType().getType() == TokenType.VOIDTK ? VoidType.voidType : IntegerType.i32;
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getParams()) {
                argTypes.add(getFuncFParamType(funcFParam));
            }
        }
        FuncType funcType = new FuncType(retType, argTypes);
        Function function = InstrBuilder.buildFunction(name, funcType);
        curFunction = function;
        symbolTableManager.addSymbol(name, function);
        symbolTableManager.addTable();
        curBlock = InstrBuilder.buildBasicBlock(function);
        if (funcDef.getFuncFParams() != null) {
            int index = 0;
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getParams()) {
                visitFuncFParam(funcFParam, function.getArgument(index++));
            }
        }
        visitBlock(funcDef.getBlock());
        symbolTableManager.removeTable();
    }

    private Type getFuncFParamType(FuncFParam funcFParam) {
        if (!funcFParam.isArray()) {
            return IntegerType.i32;
        } else {
            isCalculable = true;
            Type type = IntegerType.i32;
            for (ConstExp constExp : funcFParam.getConstExps()) {
                visitConstExp(constExp);
                type = new ArrayType(type, immediate);
            }
            isCalculable = false;
            return new PointerType(type);
        }
    }

    private void visitFuncFParam(FuncFParam funcFParam, Argument arg) {
        String name = funcFParam.getIdent().getValue();
        symbolTableManager.addSymbol(name, InstrBuilder.buildVar(arg.getType(), arg, curBlock));
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        FuncType funcType = InstrBuilder.buildFuncType(new IntegerType(32), new ArrayList<>());
        Function function = InstrBuilder.buildFunction("main", funcType);
        curFunction = function;
        symbolTableManager.addSymbol("main", function);
        symbolTableManager.addTable();
        curBlock = InstrBuilder.buildBasicBlock(curFunction);
        visitBlock(mainFuncDef.getBlock());
        symbolTableManager.removeTable();
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
        if (stmt instanceof BlockStmt blockStmt) {
            symbolTableManager.addTable();
            visitBlock(blockStmt.getBlock());
            symbolTableManager.removeTable();
        } else if (stmt instanceof ContinueStmt) {
            visitContinueStmt();
        } else if (stmt instanceof LValExpStmt lValExpStmt) {
            visitLValAssign(lValExpStmt.getlVal(), lValExpStmt.getExp());
        } else if (stmt instanceof BreakStmt) {
            visitBreakStmt();
        } else if (stmt instanceof ReturnStmt returnStmt) {
            if (returnStmt.getExp() != null) {
                visitExp(returnStmt.getExp());
                InstrBuilder.buildRetInst(curBlock, tempValue);
            } else {
                InstrBuilder.buildRetInst(curBlock);
            }
        } else if (stmt instanceof IfStmt ifStmt) {
            visitIfStmt(ifStmt);
        } else if (stmt instanceof ForStruct forStruct) {
            visitForStruct(forStruct);
        } else if (stmt instanceof ExpStmt expStmt) {
            visitExp(expStmt.getExp());
        } else if (stmt instanceof GetintStmt getintStmt) {
            visitGetintStmt(getintStmt);
        } else if (stmt instanceof PrintfStmt printfStmt) {
            visitPrintfStruct(printfStmt);
        }
    }


    private void visitForStmt(ForStmt forStmt) {
        if (forStmt != null) {
            visitLValAssign(forStmt.getlVal(), forStmt.getExp());
        }
    }

    private void visitLValAssign(LVal lVal, Exp exp) {
        visitExp(exp);
        Value ans = tempValue;
        doLValAssign(lVal, ans);
    }

    private void visitGetintStmt(GetintStmt getintStmt) {
        LVal lVal = getintStmt.getlVal();
        Function function = (Function) symbolTableManager.getValue("getint");
        Value ans = InstrBuilder.buildCallInst(curBlock, function, new ArrayList<>());
        doLValAssign(lVal, ans);
    }

    private void doLValAssign(LVal lVal, Value ans) {
        Value pointer = symbolTableManager.getValue(lVal.getIdent().getValue());
        if (!lVal.getExps().isEmpty()) {
            Type type = pointer.getType();
            Type target = ((PointerType) type).getTargetType();
            ArrayList<Value> indices = new ArrayList<>();
            if (target instanceof PointerType) {
                pointer = InstrBuilder.buildLoadInst(curBlock, pointer);
            } else {
                indices.add(ConstInt.ZERO);
            }
            for (Exp exp : lVal.getExps()) {
                visitExp(exp);
                indices.add(tempValue);
            }
            pointer = InstrBuilder.buildGEPInst(pointer, indices, curBlock);
        }
        tempValue = InstrBuilder.buildStoreInst(curBlock, ans, pointer);
    }

    private void visitContinueStmt() {
        InstrBuilder.buildBrInst(curBlock, curEndBlock);
    }

    private void visitBreakStmt() {
        InstrBuilder.buildBrInst(curBlock, curForEndBlock);
    }

    private void visitForStruct(ForStruct forStruct) {
        BasicBlock tmpTrueBlock = curTrueBlock;
        BasicBlock tmpFalseBlock = curFalseBlock;
        visitForStmt(forStruct.getForStmt1());
        BasicBlock condBlock = InstrBuilder.buildBasicBlock(curFunction);
        BasicBlock endBlock = InstrBuilder.buildUnnamedBasicBlock();
        BasicBlock trueBlock = InstrBuilder.buildUnnamedBasicBlock();
        BasicBlock falseBlock = InstrBuilder.buildUnnamedBasicBlock();
        InstrBuilder.buildBrInst(curBlock, condBlock);
        curBlock = condBlock;
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        curEndBlock = endBlock;
        curForEndBlock = falseBlock;
        if (forStruct.getCond() != null) {
            visitCond(forStruct.getCond());
        } else {
            InstrBuilder.buildBrInst(curBlock, trueBlock);
        }
        trueBlock.refill(curFunction);
        curBlock = trueBlock;
        visitStmt(forStruct.getStmt());
        InstrBuilder.buildBrInst(curBlock, endBlock);
        endBlock.refill(curFunction);
        curBlock = endBlock;
        visitForStmt(forStruct.getForStmt2());
        InstrBuilder.buildBrInst(curBlock, condBlock);
        falseBlock.refill(curFunction);
        curBlock = falseBlock;
        curTrueBlock = tmpTrueBlock;
        curFalseBlock = tmpFalseBlock;
    }

    private void visitIfStmt(IfStmt ifStmt) {
        BasicBlock tmpTrueBlock = curTrueBlock;
        BasicBlock tmpFalseBlock = curFalseBlock;
        BasicBlock trueBlock = InstrBuilder.buildUnnamedBasicBlock();
        BasicBlock falseBlock = InstrBuilder.buildUnnamedBasicBlock();
        if (ifStmt.getStmt2() == null) {
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());
            trueBlock.refill(curFunction);
            curBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            InstrBuilder.buildBrInst(curBlock, falseBlock);
            falseBlock.refill(curFunction);
            curBlock = falseBlock;
        } else {
            BasicBlock endBlock = InstrBuilder.buildUnnamedBasicBlock();
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());
            trueBlock.refill(curFunction);
            curBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            InstrBuilder.buildBrInst(curBlock, endBlock);
            falseBlock.refill(curFunction);
            curBlock = falseBlock;
            visitStmt(ifStmt.getStmt2());
            InstrBuilder.buildBrInst(curBlock, endBlock);
            endBlock.refill(curFunction);
            curBlock = endBlock;
        }
        curTrueBlock = tmpTrueBlock;
        curFalseBlock = tmpFalseBlock;
    }

    private void visitPrintfStruct(PrintfStmt printfStmt) {
        Function putCh = (Function) symbolTableManager.getValue("putch");
        Function putInt = (Function) symbolTableManager.getValue("putint");
        String format = printfStmt.getFormat().getValue();
        int cnt = 0;
        for (int i = 1; i < format.length() - 1; i++) {
            char c = format.charAt(i);
            ArrayList<Value> args = new ArrayList<>();
            if (c == '%') {
                i++;
                visitExp(printfStmt.getExps().get(cnt++));
                args.add(tempValue);
                tempValue = InstrBuilder.buildCallInst(curBlock, putInt, args);
            } else if (c == '\\') {
                i++;
                args.add(InstrBuilder.buildConstInt('\n'));
                tempValue = InstrBuilder.buildCallInst(curBlock, putCh, args);
            } else {
                args.add(InstrBuilder.buildConstInt(c));
                tempValue = InstrBuilder.buildCallInst(curBlock, putCh, args);
            }
        }
    }


    private void visitCond(Cond cond) {
        visitLOrExp(cond.getlOrExp());
    }

    private void visitLOrExp(LOrExp lOrExp) {
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        int length = lOrExp.getlAndExps().size();
        for (int i = 0; i < length - 1; i++) {
            BasicBlock thenBlock = InstrBuilder.buildUnnamedBasicBlock();
            curFalseBlock = thenBlock;
            visitLAndExp(lOrExp.getlAndExps().get(i));
            thenBlock.refill(curFunction);
            curBlock = thenBlock;
        }
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        visitLAndExp(lOrExp.getlAndExps().get(length - 1));
    }

    private void visitLAndExp(LAndExp lAndExp) {
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        int length = lAndExp.getEqExps().size();
        for (int i = 0; i < length - 1; i++) {
            visitEqExp(lAndExp.getEqExps().get(i));
            BasicBlock thenBlock = InstrBuilder.buildBasicBlock(curFunction);
            InstrBuilder.buildBrInst(curBlock, thenBlock, curFalseBlock, tempValue);
            curBlock = thenBlock;
        }
        visitEqExp(lAndExp.getEqExps().get(length - 1));
        InstrBuilder.buildBrInst(curBlock, trueBlock, falseBlock, tempValue);
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
    }

    private void visitEqExp(EqExp eqExp) {
        visitRelExp(eqExp.getRelExps().get(0));
        if (eqExp.getRelExps().size() == 1 && tempValue instanceof Assignable) {
            if (!(tempValue instanceof BinaryInst binaryInst && binaryInst.isLogical())) {
                tempValue = InstrBuilder.buildBinaryInst(curBlock, Operator.ICMP_NE, tempValue, ConstInt.ZERO);
            }
            return;
        }
        for (int i = 1; i < eqExp.getRelExps().size(); i++) {
            Value prev = tempValue;
            visitRelExp(eqExp.getRelExps().get(i));
            Operator op = switch (eqExp.getOps().get(i - 1).getType()) {
                case EQL -> Operator.ICMP_EQ;
                case NEQ -> Operator.ICMP_NE;
                default -> throw new IllegalStateException();
            };
            tempValue = InstrBuilder.buildBinaryInst(curBlock, op, prev, tempValue);
        }
    }

    private void visitRelExp(RelExp relExp) {
        visitAddExp(relExp.getAddExps().get(0));
        for (int i = 1; i < relExp.getAddExps().size(); i++) {
            Value prev = tempValue;
            visitAddExp(relExp.getAddExps().get(i));
            Operator op = switch (relExp.getOps().get(i - 1).getType()) {
                case LSS -> Operator.ICMP_SLT;
                case LEQ -> Operator.ICMP_SLE;
                case GRE -> Operator.ICMP_SGT;
                case GEQ -> Operator.ICMP_SGE;
                default -> throw new IllegalStateException();
            };
            tempValue = InstrBuilder.buildBinaryInst(curBlock, op, prev, tempValue);
        }
    }

    private void visitExp(Exp exp) {
        if (exp != null) {
            visitAddExp(exp.getAddExp());
        }
    }

    private void visitAddExp(AddExp addExp) {
        ArrayList<MulExp> mulExps = addExp.getMulExps();
        visitMulExp(mulExps.get(0));
        for (int i = 1; i < mulExps.size(); i++) {
            int im = immediate;
            Value prev = tempValue;
            visitMulExp(mulExps.get(i));
            if (isGlobal || isCalculable) {
                immediate = cal(im, immediate, addExp.getOps().get(i - 1).getType());
            } else {
                Operator op = switch (addExp.getOps().get(i - 1).getType()) {
                    case PLUS -> Operator.ADD;
                    case MINU -> Operator.SUB;
                    default -> throw new IllegalStateException();
                };
                tempValue = InstrBuilder.buildBinaryInst(curBlock, op, prev, tempValue);
            }
        }
    }

    private void visitMulExp(MulExp mulExp) {
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExps();
        ArrayList<Token> ops = mulExp.getOps();
        visitUnaryExp(unaryExps.get(0));
        for (int i = 1; i < unaryExps.size(); i++) {
            Value prev = tempValue;
            int im = immediate;
            visitUnaryExp(unaryExps.get(i));
            if (isGlobal || isCalculable) {
                immediate = cal(im, immediate, mulExp.getOps().get(i - 1).getType());
            } else {
                Operator op = switch (ops.get(i - 1).getType()) {
                    case MULT -> Operator.MUL;
                    case DIV -> Operator.SDIV;
                    case MOD -> Operator.SREM;
                    default -> throw new IllegalStateException();
                };
                tempValue = InstrBuilder.buildBinaryInst(curBlock, op, prev, tempValue);
            }
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
                    tempValue = InstrBuilder.buildBinaryInst(curBlock, Operator.SUB,
                            ConstInt.ZERO, tempValue);
                }
            } else if (op == TokenType.NOT) {
                tempValue = InstrBuilder.buildBinaryInst(curBlock, Operator.ICMP_EQ,
                        ConstInt.ZERO, tempValue);
            }
        } else {
            String name = unaryExp.getIdent().getValue();
            Function func = (Function) symbolTableManager.getValue(name);
            ArrayList<Value> args = new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                for (Exp exp : unaryExp.getFuncRParams().getExps()) {
                    visitExp(exp);
                    args.add(tempValue);
                }
            }
            tempValue = InstrBuilder.buildCallInst(curBlock, func, args);
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
        if (isGlobal || isCalculable) {
            immediate = number.getNumber();
        } else {
            tempValue = new ConstInt(number.getNumber());
        }
    }

    private void visitLVal(LVal lVal) {
        String name = lVal.getIdent().getValue();
        Value pointer = symbolTableManager.getValue(name);
        if (isGlobal || isCalculable) {
            if (lVal.getExps().isEmpty()) {
                if (pointer instanceof GlobalVar) {
                    immediate = ((ConstInt) ((GlobalVar) pointer).getValue()).getIntValue();
                } else {
                    immediate = symbolTableManager.getConst(name);
                }
            } else {
                tempValue = ((GlobalVar) pointer).getValue();
                for (Exp exp : lVal.getExps()) {
                    visitExp(exp);
                    tempValue = ((ConstArray) tempValue).getValues().get(immediate);
                }
                immediate = ((ConstInt) tempValue).getIntValue();
            }
        } else {
            ArrayList<Value> indexes = new ArrayList<>();
            if (lVal.getExps().isEmpty()) {
                if (!(((PointerType) pointer.getType()).getTargetType() instanceof ArrayType)) {
                    tempValue = InstrBuilder.buildLoadInst(curBlock, pointer);
                } else {
                    indexes.add(ConstInt.ZERO);
                    indexes.add(ConstInt.ZERO);
                    tempValue = InstrBuilder.buildGEPInst(pointer, indexes, curBlock);
                }
            } else {
                Type type = pointer.getType();
                Type targetType = ((PointerType) type).getTargetType();
                if (targetType instanceof PointerType) {
                    pointer = InstrBuilder.buildLoadInst(curBlock, pointer);
                } else {
                    indexes.add(ConstInt.ZERO);
                }
                for (Exp exp : lVal.getExps()) {
                    visitExp(exp);
                    indexes.add(tempValue);
                }
                pointer = InstrBuilder.buildGEPInst(pointer, indexes, curBlock);
                if (((PointerType) pointer.getType()).getTargetType() instanceof ArrayType) {
                    ArrayList<Value> subIndices = new ArrayList<>();
                    subIndices.add(ConstInt.ZERO);
                    subIndices.add(ConstInt.ZERO);
                    tempValue = InstrBuilder.buildGEPInst(pointer, subIndices, curBlock);
                } else {
                    tempValue = InstrBuilder.buildLoadInst(curBlock, pointer);
                }
            }
        }
    }
}
