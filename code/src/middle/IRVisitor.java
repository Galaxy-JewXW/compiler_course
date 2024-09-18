package middle;

import frontend.syntax.Character;
import frontend.syntax.Number;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.FuncDef;
import frontend.syntax.function.FuncFParam;
import frontend.syntax.function.MainFuncDef;
import frontend.syntax.statement.*;
import frontend.syntax.variable.*;
import frontend.token.TokenType;
import middle.component.*;
import middle.component.instructions.OperatorType;
import middle.component.model.Value;
import middle.component.types.*;
import tools.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRVisitor {
    private final SymbolTable symbolTable = new SymbolTable();
    private final CompUnit compUnit;
    private Function curFunction = null;
    private BasicBlock curBlock = null;
    private BasicBlock curTrueBlock = null;
    private BasicBlock curFalseBlock = null;
    private BasicBlock curEndBlock = null;
    private BasicBlock curForEndBlock = null;
    private int immediate = 0;
    private Value tempValue = null;
    private ValueType tempValueType = null;
    private boolean isGlobal = false;
    private boolean isCalculable = false;
    // 用于printf时暂时储存字符串
    private final HashMap<String, Integer> stringMap = new HashMap<>();
    int stringCount = 0;

    public IRVisitor(CompUnit root) {
        this.compUnit = root;
    }

    public void build() {
        visitCompUnit();
    }

    private void visitCompUnit() {
        symbolTable.addTable();
        symbolTable.addSymbol("getint", Builder.buildBuiltInFunc("getint",
                IntegerType.i32, new ArrayList<>()));
        symbolTable.addSymbol("getchar", Builder.buildBuiltInFunc("getchar",
                IntegerType.i32, new ArrayList<>()));
        symbolTable.addSymbol("putint", Builder.buildBuiltInFunc("putint",
                VoidType.VOID, IntegerType.i32));
        symbolTable.addSymbol("putch", Builder.buildBuiltInFunc("putch",
                VoidType.VOID, IntegerType.i32));
        symbolTable.addSymbol("putstr", Builder.buildBuiltInFunc("putstr",
                VoidType.VOID, new PointerType(IntegerType.i8)));
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
            tempValueType = constDecl.getBType().getToken().getType() == TokenType.INTTK
                    ? IntegerType.i32 : IntegerType.i8;
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDef constDef) {
        String name = constDef.getIdent().getContent();
        isCalculable = true;
        if (constDef.getConstExp() == null) {
            // 对应普通变量的定义
            visitConstInitVal(constDef.getConstInitVal(), 0);
            int number = immediate;
            if (tempValueType.equals(IntegerType.i8)) {
                number = number & 0xFF;
            }
            symbolTable.addConst(name, number);
            ConstInt constInt = Builder.buildConstInt(number, tempValueType);
            if (isGlobal) {
                tempValue = Builder.buildGlobalVar(name, tempValueType, constInt, true);
            } else {
                tempValue = Builder.buildVar(tempValueType, constInt, curBlock);
            }
        } else {
            // 对应的是一维数组的定义
            visitConstExp(constDef.getConstExp());
            int length = immediate;
            visitConstInitVal(constDef.getConstInitVal(), length);
            if (isGlobal) {
                tempValue = Builder.buildGlobalArray(name, tempValueType, tempValue, true);
            }
        }
        isCalculable = false;
        symbolTable.addSymbol(name, tempValue);
    }

    // length参数为数组定义时所设置的长度
    private void visitConstInitVal(ConstInitVal constInitVal, int length) {
        if (constInitVal.getConstExp() != null) {
            // 对应'const int a = 3;'
            visitConstExp(constInitVal.getConstExp());
            if (isGlobal || isCalculable) {
                tempValue = Builder.buildConstInt(immediate, tempValueType);
            }
        } else if (constInitVal.getStringConst() != null) {
            String stringConst = constInitVal.getStringConst().getContent();
            ConstArray constArray = new ConstArray(length);
            for (int i = 1; i < stringConst.length() - 1; i++) {
                constArray.addElement(Builder.buildConstInt(stringConst.charAt(i), IntegerType.i8));
            }
            constArray.setFilled();
            int unfilled = length - (stringConst.length() - 2);
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(Builder.buildConstInt(0, IntegerType.i8));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i8, length);
        } else if (constInitVal.getConstExps() != null) {
            ConstArray constArray = new ConstArray(length);
            for (ConstExp constExp : constInitVal.getConstExps()) {
                visitConstExp(constExp);
                constArray.addElement(Builder.buildConstInt(immediate, IntegerType.i32));
            }
            constArray.setFilled();
            int unfilled = length - constInitVal.getConstExps().size();
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(Builder.buildConstInt(0, IntegerType.i32));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i32, length);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            tempValueType = varDecl.getBType().getToken().getType() == TokenType.INTTK
                    ? IntegerType.i32 : IntegerType.i8;
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDef varDef) {
        String name = varDef.getIdent().getContent();
        if (varDef.getConstExp() == null) {
            // 无维度，对应int x = 3;
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal(), 0);
            } else {
                // 对未指定初始值的全局变量，统一初始为0
                immediate = 0;
            }
            Value initValue = Builder.buildConstInt(immediate, tempValueType);
            if (isGlobal) {
                tempValue = Builder.buildGlobalVar(name, tempValueType, initValue, false);
            } else {
                tempValue = Builder.buildVar(
                        tempValueType,
                        varDef.getInitVal() != null ? tempValue : null,
                        curBlock
                );
            }
        } else {
            isCalculable = true;
            visitConstExp(varDef.getConstExp());
            int length = immediate;
            isCalculable = false;
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal(), length);
            } else {
                tempValue = null;
                tempValueType = new ArrayType(tempValueType, length);
            }
            if (isGlobal) {
                tempValue = Builder.buildGlobalArray(name, tempValueType, tempValue, false);
            } else {
                tempValue = Builder.buildArray(tempValueType, tempValue, curBlock);
            }
        }
        symbolTable.addSymbol(name, tempValue);
    }

    private void visitInitVal(InitVal initVal, int length) {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp());
        } else if (initVal.getStringConst() != null) {
            String stringConst = initVal.getStringConst().getContent();
            ConstArray constArray = new ConstArray(length);
            for (int i = 1; i < stringConst.length() - 1; i++) {
                constArray.addElement(Builder.buildConstInt(stringConst.charAt(i), IntegerType.i8));
            }
            constArray.setFilled();
            int unfilled = length - (stringConst.length() - 2);
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(Builder.buildConstInt(0, IntegerType.i8));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i8, length);
        } else if (initVal.getExps() != null) {
            ConstArray constArray = new ConstArray(length);
            for (Exp exp : initVal.getExps()) {
                visitExp(exp);
                if (isGlobal) {
                    tempValue = Builder.buildConstInt(immediate, IntegerType.i32);
                }
                constArray.addElement(tempValue);
            }
            constArray.setFilled();
            int unfilled = length - initVal.getExps().size();
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(Builder.buildConstInt(0, IntegerType.i32));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i32, length);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private void visitFuncDef(FuncDef funcDef) {
        String functionName = funcDef.getIdent().getContent();
        // 记录函数定义时形参的类型，以及函数返回值的类型
        ArrayList<ValueType> argumentsTypes = new ArrayList<>();
        ValueType returnType = switch (funcDef.getFuncType().getFuncType().getType()) {
            case VOIDTK -> VoidType.VOID;
            case INTTK -> IntegerType.i32;
            case CHARTK -> IntegerType.i8;
            default -> throw new RuntimeException("Shouldn't reach here");
        };
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                argumentsTypes.add(getFuncFParamType(funcFParam));
            }
        }
        Function function = Builder.buildFunction(functionName, returnType, argumentsTypes);
        curFunction = function;
        symbolTable.addSymbol(functionName, function);
        symbolTable.addTable();
        curBlock = Builder.buildBasicBlock(function);
        if (funcDef.getFuncFParams() != null) {
            for (int i = 0; i < funcDef.getFuncFParams().getFuncFParams().size(); i++) {
                FuncFParam funcFParam = funcDef.getFuncFParams().getFuncFParams().get(i);
                visitFuncFParam(funcFParam, function.getArguments().get(i));
            }
        }
        visitBlock(funcDef.getBlock());
        symbolTable.removeTable();
    }

    private ValueType getFuncFParamType(FuncFParam funcFParam) {
        ValueType baseType = switch (funcFParam.getBType().getToken().getType()) {
            case INTTK -> IntegerType.i32;
            case CHARTK -> IntegerType.i8;
            default -> throw new RuntimeException("Shouldn't reach here");
        };
        if (funcFParam.isArray()) {
            return new PointerType(baseType);
        } else {
            return baseType;
        }
    }

    private void visitFuncFParam(FuncFParam funcFParam, Argument argument) {
        String paramName = funcFParam.getIdent().getContent();
        // 将形参上的值复制到自己的栈帧上
        symbolTable.addSymbol(paramName, Builder.buildVar(
                argument.getValueType(), argument, curBlock
        ));
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        Function function = Builder.buildFunction("main", IntegerType.i32, new ArrayList<>());
        curFunction = function;
        symbolTable.addSymbol("main", function);
        symbolTable.addTable();
        curBlock = Builder.buildBasicBlock(curFunction);
        visitBlock(mainFuncDef.getBlock());
        symbolTable.removeTable();
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
            symbolTable.addTable();
            visitBlock(blockStmt.getBlock());
            symbolTable.removeTable();
        } else if (stmt instanceof ReturnStmt returnStmt) {
            visitReturnStmt(returnStmt);
        } else if (stmt instanceof ExpStmt expStmt) {
            visitExp(expStmt.getExp());
        } else if (stmt instanceof LValExpStmt lValExpStmt) {
            visitLValAssignStruct(lValExpStmt.getLVal(), lValExpStmt.getExp());
        } else if (stmt instanceof GetintStmt getintStmt) {
            visitGetintStmt(getintStmt);
        } else if (stmt instanceof GetcharStmt getcharStmt) {
            visitGetcharStmt(getcharStmt);
        } else if (stmt instanceof PrintfStmt printfStmt) {
            visitPrintfStmt(printfStmt);
        }
    }

    private void visitReturnStmt(ReturnStmt returnStmt) {
        if (returnStmt.getExp() != null) {
            visitExp(returnStmt.getExp());
            Builder.buildRetInst(curBlock, tempValue);
        } else {
            Builder.buildRetInst(curBlock);
        }
    }

    private void visitGetintStmt(GetintStmt getintStmt) {
        LVal lVal = getintStmt.getLVal();
        Function getInt = (Function) symbolTable.getSymbol("getint");
        Value callInst = Builder.buildCallInst(getInt, new ArrayList<>(), curBlock);
        doLValAssign(lVal, callInst);
    }

    private void visitGetcharStmt(GetcharStmt getcharStmt) {
        LVal lVal = getcharStmt.getLVal();
        Function getChar = (Function) symbolTable.getSymbol("getchar");
        Value callInst = Builder.buildCallInst(getChar, new ArrayList<>(), curBlock);
        doLValAssign(lVal, callInst);
    }

    private void visitPrintfStmt(PrintfStmt printfStmt) {
        Function putCh = (Function) symbolTable.getSymbol("putch");
        Function putStr = (Function) symbolTable.getSymbol("putstr");
        Function putInt = (Function) symbolTable.getSymbol("putint");
        String formatString = printfStmt.getStringConst().getContent();
        formatString = formatString.substring(1, formatString.length() - 1);
        formatString = formatString.replace("\\n", "\n");
        Pattern pattern = Pattern.compile("%[cd]");
        Matcher matcher = pattern.matcher(formatString);
        int pos = 0;
        int cnt = 0;
        while (matcher.find()) {
            // typeString是"%d"或"%c"
            String typeString = matcher.group();
            int start = matcher.start();
            String tempString = formatString.substring(pos, start);
            ArrayList<Value> args = new ArrayList<>();
            if (tempString.length() == 1) {
                args.add(Builder.buildConstInt(tempString.charAt(0), IntegerType.i8));
                tempValue = Builder.buildCallInst(putCh, args, curBlock);
            } else {
                Value strValue = symbolTable.getSymbol(checkStringName(tempString));
                args.add(Builder.buildGEPInst(strValue, ConstInt.i32ZERO, curBlock));
                tempValue = Builder.buildCallInst(putStr, args, curBlock);
            }
            if (typeString.equals("%d")) {
                visitExp(printfStmt.getExps().get(cnt++));
                ArrayList<Value> arguments = new ArrayList<>();
                arguments.add(tempValue);
                if (tempValue.getValueType().equals(IntegerType.i8)) {
                    if (tempValue instanceof ConstInt constInt) {
                        tempValue = Builder.buildConstInt(constInt.getIntValue(), IntegerType.i32);
                    } else {
                        tempValue = Builder.buildZextInst(tempValue, IntegerType.i32, curBlock);
                    }
                }
                tempValue = Builder.buildCallInst(putInt, arguments, curBlock);
            } else if (typeString.equals("%c")) {
                visitExp(printfStmt.getExps().get(cnt++));
                ArrayList<Value> arguments = new ArrayList<>();
                arguments.add(tempValue);
                if (tempValue.getValueType().equals(IntegerType.i32)) {
                    if (tempValue instanceof ConstInt constInt) {
                        tempValue = Builder.buildConstInt(constInt.getIntValue(), IntegerType.i8);
                    } else {
                        tempValue = Builder.buildTruncInst(tempValue, IntegerType.i8, curBlock);
                    }
                }
                tempValue = Builder.buildCallInst(putCh, arguments, curBlock);
            }
            pos = start + 2;
        }
        if (pos < formatString.length()) {
            String tempString = formatString.substring(pos);
            ArrayList<Value> args = new ArrayList<>();
            if (tempString.length() == 1) {
                args.add(Builder.buildConstInt(tempString.charAt(0), IntegerType.i8));
                tempValue = Builder.buildCallInst(putCh, args, curBlock);
            } else {
                Value strValue = symbolTable.getSymbol(checkStringName(tempString));
                args.add(Builder.buildGEPInst(strValue, ConstInt.i32ZERO, curBlock));
                tempValue = Builder.buildCallInst(putStr, args, curBlock);
            }
        }
    }

    private int getStringIndex(String string) {
        if (stringMap.containsKey(string)) {
            return stringMap.get(string);
        }
        ConstString constString = new ConstString(string);
        ArrayType arrayType = new ArrayType(IntegerType.i8, constString.getLength());
        String idString = getStringIdString(stringCount);
        Value value = Builder.buildGlobalVar(idString, arrayType, constString, true);
        symbolTable.addGlobalSymbol(idString, value);
        stringMap.put(string, stringCount);
        return stringCount++;
    }

    private String getStringIdString(int index) {
        return ".str." + index;
    }

    private String checkStringName(String string) {
        return getStringIdString(getStringIndex(string));
    }

    // 把forStmt和LValExpStmt结合在一起
    private void visitLValAssignStruct(LVal lVal, Exp exp) {
        visitExp(exp);
        Value result = tempValue;
        doLValAssign(lVal, result);
    }

    private void doLValAssign(LVal lVal, Value result) {
        Value pointer = symbolTable.getSymbol(lVal.getIdent().getContent());
        if (lVal.getExp() != null
                && pointer.getValueType() instanceof PointerType) {
            visitExp(lVal.getExp());
            pointer = Builder.buildGEPInst(pointer, tempValue, curBlock);
        }
        tempValue = Builder.buildStoreInst(result, pointer, curBlock);
    }

    private void visitExp(Exp exp) {
        if (exp != null) {
            visitAddExp(exp.getAddExp());
        }
    }

    private void visitLVal(LVal lVal) {
        String name = lVal.getIdent().getContent();
        Value pointer = symbolTable.getSymbol(name);
        if (isGlobal || isCalculable) {
            // 考虑const int b = 3; int c = b;
            // 或者是const int N = 2; int aa[N];
            if (lVal.getExp() == null) {
                if (pointer instanceof GlobalVar globalVar) {
                    // 全局const，满足isGlobal
                    immediate = ((ConstInt) globalVar.getValue()).getIntValue();
                } else {
                    // 局部const，满足isCalculable
                    immediate = symbolTable.getConst(name);
                }
            } else {
                tempValue = ((GlobalVar) pointer).getValue();
                visitExp(lVal.getExp());
                tempValue = ((ConstArray) tempValue).getElements().get(immediate);
                immediate = ((ConstInt) tempValue).getIntValue();
            }
        } else {
            if (lVal.getExp() == null) {
                // lVal为0维标识符, int a; a = 4;
                if (!(((PointerType) pointer.getValueType()).getTargetType() instanceof ArrayType)) {
                    tempValue = Builder.buildLoadInst(pointer, curBlock);
                } else {
                    throw new RuntimeException("Shouldn't reach here");
                }
            } else {
                visitExp(lVal.getExp());
                Value pos = Builder.buildGEPInst(pointer, tempValue, curBlock);
                tempValue = Builder.buildLoadInst(pos, curBlock);
            }
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else if (primaryExp.getCharacter() != null) {
            visitCharacter(primaryExp.getCharacter());
        } else if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp());
        } else if (primaryExp.getLVal() != null) {
            visitLVal(primaryExp.getLVal());
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private void visitNumber(Number number) {
        if (isGlobal || isCalculable) {
            immediate = number.getIntConstValue();
        } else {
            tempValue = Builder.buildConstInt(number.getIntConstValue(),
                    IntegerType.i32);
        }
    }

    private void visitCharacter(Character character) {
        if (isGlobal || isCalculable) {
            immediate = character.getCharConstValue();
        } else {
            tempValue = Builder.buildConstInt(character.getCharConstValue(),
                    IntegerType.i8);
        }
    }

    private void visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryExp() != null) {
            visitUnaryExp(unaryExp.getUnaryExp());
            TokenType op = unaryExp.getUnaryOp().getOperator().getType();
            if (op == TokenType.MINU) {
                if (isGlobal || isCalculable) {
                    immediate = -immediate;
                } else {
                    tempValue = Builder.buildBinaryInst(ConstInt.i32ZERO, OperatorType.SUB,
                            tempValue, curBlock);
                }
            } else if (op == TokenType.NOT) {
                tempValue = Builder.buildBinaryInst(ConstInt.i32ZERO, OperatorType.ICMP_EQ,
                        tempValue, curBlock);
            }
        } else if (unaryExp.getIdent() != null) {
            // 函数调用：a(10, 20, a(1, 2, 3))
            String functionName = unaryExp.getIdent().getContent();
            Function function = (Function) symbolTable.getSymbol(functionName);
            // parameters是进行函数调用时所使用的实参
            ArrayList<Value> parameters = new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                for (int i = 0; i < unaryExp.getFuncRParams().getExps().size(); i++) {
                    Exp exp = unaryExp.getFuncRParams().getExps().get(i);
                    visitExp(exp);
                    // 判断是否需要进行类型转换
                    Value cacheValue = tempValue;
                    if (function.getArguments().get(i).getValueType().equals(IntegerType.i8)
                            && tempValue.getValueType().equals(IntegerType.i32)) {
                        // int型变量当作char型作为实参，需要进行截断
                        if (tempValue instanceof ConstInt constInt) {
                            cacheValue = Builder.buildConstInt(constInt.getIntValue(), IntegerType.i8);
                        } else {
                            cacheValue = Builder.buildTruncInst(tempValue, IntegerType.i8, curBlock);
                        }
                    }
                    if (function.getArguments().get(i).getValueType().equals(IntegerType.i32)
                            && tempValue.getValueType().equals(IntegerType.i8)) {
                        // char型变量当作int型作为实参，需要进行扩展
                        if (tempValue instanceof ConstInt constInt) {
                            cacheValue = Builder.buildConstInt(constInt.getIntValue(), IntegerType.i32);
                        } else {
                            cacheValue = Builder.buildZextInst(tempValue, IntegerType.i32, curBlock);
                        }
                    }
                    parameters.add(cacheValue);
                }
            }
            tempValue = Builder.buildCallInst(function, parameters, curBlock);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private void visitMulExp(MulExp mulExp) {
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExps();
        visitUnaryExp(unaryExps.get(0));
        for (int i = 1; i < unaryExps.size(); i++) {
            int curImmediate = immediate;
            Value curValue = tempValue;
            visitUnaryExp(unaryExps.get(i));
            if (isGlobal || isCalculable) {
                immediate = Builder.calculate(curImmediate, immediate,
                        mulExp.getOperators().get(i - 1).getType());
            } else {
                OperatorType op = switch (mulExp.getOperators().get(i - 1).getType()) {
                    case MULT -> OperatorType.MUL;
                    case DIV -> OperatorType.SDIV;
                    case MOD -> OperatorType.SREM;
                    default -> throw new IllegalStateException("Unexpected value: " +
                            mulExp.getOperators().get(i - 1).getType());
                };
                if (tempValue instanceof ConstInt constInt
                        && constInt.getValueType() == IntegerType.i8) {
                    constInt.setValueType(IntegerType.i32);
                }
                if (curValue instanceof ConstInt constInt
                        && constInt.getValueType() == IntegerType.i8) {
                    constInt.setValueType(IntegerType.i32);
                }
                tempValue = Builder.buildBinaryInst(curValue, op, tempValue, curBlock);
            }
        }
    }

    private void visitAddExp(AddExp addExp) {
        ArrayList<MulExp> mulExps = addExp.getMulExps();
        visitMulExp(mulExps.get(0));
        for (int i = 1; i < mulExps.size(); i++) {
            int curImmediate = immediate;
            Value curValue = tempValue;
            visitMulExp(mulExps.get(i));
            if (isGlobal || isCalculable) {
                immediate = Builder.calculate(curImmediate, immediate,
                        addExp.getOperators().get(i - 1).getType());
            } else {
                OperatorType op = switch (addExp.getOperators().get(i - 1).getType()) {
                    case PLUS -> OperatorType.ADD;
                    case MINU -> OperatorType.SUB;
                    default -> throw new IllegalStateException("Unexpected operator type: " +
                            addExp.getOperators().get(i - 1).getType());
                };
                if (tempValue instanceof ConstInt constInt
                        && constInt.getValueType() == IntegerType.i8) {
                    constInt.setValueType(IntegerType.i32);
                }
                if (curValue instanceof ConstInt constInt
                        && constInt.getValueType() == IntegerType.i8) {
                    constInt.setValueType(IntegerType.i32);
                }
                tempValue = Builder.buildBinaryInst(curValue, op, tempValue, curBlock);
            }
        }
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }

}
