package middle;

import frontend.SymbolTable;
import frontend.TableManager;
import frontend.symbol.FuncSymbol;
import frontend.symbol.VarSymbol;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.*;
import frontend.syntax.statement.*;
import frontend.syntax.variable.*;
import frontend.token.TokenType;
import middle.component.*;
import middle.component.instruction.*;
import middle.component.instruction.io.*;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;
import tools.StrToArray;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IRBuilder {
    private final SymbolTable rootTable = TableManager.getInstance()
            .getCurrentTable();
    private SymbolTable currentTable = rootTable;
    private final CompUnit compUnit;
    private Function currentFunction = null;
    private boolean isGlobal = false;

    public IRBuilder(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void build() {
        buildCompUnit();
    }

    private void buildCompUnit() {
        isGlobal = true;
        for (Decl decl : compUnit.getDecls()) {
            buildDecl(decl);
        }
        isGlobal = false;
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            buildFuncDef(funcDef);
        }
        buildMainFuncDef(compUnit.getMainFuncDef());
    }

    private void buildDecl(Decl decl) {
        if (decl instanceof ConstDecl constDecl) {
            buildConstDecl(constDecl);
        } else if (decl instanceof VarDecl varDecl) {
            buildVarDecl(varDecl);
        }
    }

    private void buildConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            buildConstDef(constDef);
        }
    }

    private void buildConstDef(ConstDef constDef) {
        VarSymbol varSymbol = (VarSymbol) currentTable.getSymbol(
                constDef.getIdent().getContent());
        // constDef必然有constInitVal，所以也必然有initialValue
        InitialValue initialValue = varSymbol.getInitialValue();
        if (isGlobal) {
            String name = "@" + constDef.getIdent().getContent();
            ValueType type = new PointerType(initialValue.getValueType());
            GlobalVar globalVar = new GlobalVar(name, type, initialValue);
            varSymbol.setLlvmValue(globalVar);
        } else {
            Instruction instruction = new AllocInst(IRData.getLocalVarName(
                    currentFunction), initialValue.getValueType());
            varSymbol.setLlvmValue(instruction);
            if (varSymbol.getDimension() == 0) {
                int init = initialValue.getElements().get(0);
                new StoreInst(IRData.getLocalVarName(currentFunction),
                        instruction, new ConstInt(initialValue.getValueType(), init));
            } else if (varSymbol.getDimension() == 1) {
                Value pointer = instruction;
                for (int i = 0; i < initialValue.getElements().size(); i++) {
                    instruction = new GepInst(IRData.getLocalVarName(currentFunction),
                            pointer,
                            new ConstInt(IntegerType.i32, i)
                    );
                    new StoreInst(IRData.getLocalVarName(currentFunction),
                            instruction,
                            new ConstInt(IntegerType.i32, initialValue.getElements().get(i)));
                }
            } else {
                throw new RuntimeException("Shouldn't reach here");
            }
        }
    }

    private void buildVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            try {
                buildVarDef(varDef);
            } catch (NullPointerException e) {
                System.out.println(varDef.getIdent().getContent());
                currentTable.print();
            }
        }
    }

    private void buildVarDef(VarDef varDef) throws NullPointerException {
        VarSymbol varSymbol = (VarSymbol) currentTable.getSymbol(
                varDef.getIdent().getContent());
        InitialValue initialValue = varSymbol.getInitialValue();
        if (isGlobal) {
            // 全局下initialValue不为null
            // initialValue中的elements数组可能为null
            String name = "@" + varDef.getIdent().getContent();
            ValueType type = new PointerType(initialValue.getValueType());
            GlobalVar globalVar = new GlobalVar(name, type, initialValue);
            varSymbol.setLlvmValue(globalVar);
        } else {
            AllocInst instruction;
            ValueType valueType = switch (varSymbol.getType()) {
                case INT -> IntegerType.i32;
                case CHAR -> IntegerType.i8;
                default -> throw new RuntimeException("Shouldn't reach here");
            };
            if (varSymbol.getDimension() == 0) {
                instruction = new AllocInst(IRData.getLocalVarName(
                        currentFunction),
                        valueType);
                varSymbol.setLlvmValue(instruction);
                if (varDef.getInitVal() != null) {
                    ArrayList<Value> inits = buildInitVal(varDef.getInitVal());
                    Value storeValue = inits.get(0);
                    ValueType targetType = instruction.getTargetType();
                    // 进行类型转换
                    if (storeValue.getValueType().equals(IntegerType.i32)
                            && targetType.equals(IntegerType.i8)) {
                        if (storeValue instanceof ConstInt constInt) {
                            storeValue = new ConstInt(IntegerType.i8, constInt.getIntValue());
                        } else {
                            storeValue = new TruncInst(IRData.getLocalVarName(currentFunction),
                                    storeValue, IntegerType.i8);
                        }
                    } else if (storeValue.getValueType().equals(IntegerType.i8)
                            && targetType.equals(IntegerType.i32)) {
                        if (storeValue instanceof ConstInt constInt) {
                            storeValue = new ConstInt(IntegerType.i32, constInt.getIntValue());
                        } else {
                            storeValue = new ZextInst(IRData.getLocalVarName(currentFunction),
                                    storeValue, IntegerType.i8);
                        }
                    }
                    new StoreInst(IRData.getLocalVarName(currentFunction), instruction,
                            storeValue);
                }
            } else {
                valueType = new ArrayType(varSymbol.getLength(), valueType);
                instruction = new AllocInst(IRData.getLocalVarName(
                        currentFunction),
                        valueType);
                varSymbol.setLlvmValue(instruction);
                if (varDef.getInitVal() != null) {
                    ArrayList<Value> inits = buildInitVal(varDef.getInitVal());
                    for (int i = 0; i < inits.size(); i++) {
                        Instruction inst = new GepInst(IRData.getLocalVarName(currentFunction), instruction,
                                new ConstInt(IntegerType.i32, i));
                        new StoreInst(IRData.getLocalVarName(currentFunction), inst,
                                inits.get(i));
                    }
                }
            }
        }
    }

    private ArrayList<Value> buildInitVal(InitVal initVal) {
        ArrayList<Value> ans = new ArrayList<>();
        if (initVal.getExp() != null) {
            ans.add(buildExp(initVal.getExp()));
        } else if (initVal.getExps() != null) {
            for (Exp exp : initVal.getExps()) {
                ans.add(buildExp(exp));
            }
        } else if (initVal.getStringConst() != null) {
            ans = StrToArray.str2Array(initVal.getStringConst().getContent())
                    .stream()
                    .map(value -> new ConstInt(IntegerType.i8, value))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
        return ans;
    }

    private Value buildExp(Exp exp) {
        return buildAddExp(exp.getAddExp());
    }

    private Value buildAddExp(AddExp addExp) {
        Value left = buildMulExp(addExp.getMulExps().get(0));
        if (left.getValueType().equals(IntegerType.i8)) {
            left = new ZextInst(IRData.getLocalVarName(currentFunction),
                    left, IntegerType.i32);
        }
        Value right;
        Instruction instruction;
        for (int i = 1; i < addExp.getMulExps().size(); i++) {
            TokenType op = addExp.getOperators().get(i - 1).getType();
            right = buildMulExp(addExp.getMulExps().get(i));
            if (right.getValueType().equals(IntegerType.i8)) {
                right = new ZextInst(IRData.getLocalVarName(currentFunction),
                        right, IntegerType.i32);
            }
            if (op == TokenType.PLUS) {
                instruction = new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ADD, left, right
                );
            } else {
                instruction = new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.SUB, left, right
                );
            }
            left = instruction;
        }
        return left;
    }

    private Value buildMulExp(MulExp mulExp) {
        Value left = buildUnaryExp(mulExp.getUnaryExps().get(0));
        if (left.getValueType().equals(IntegerType.i8)) {
            left = new ZextInst(IRData.getLocalVarName(currentFunction),
                    left, IntegerType.i32);
        }
        Value right;
        Instruction instruction;
        for (int i = 1; i < mulExp.getUnaryExps().size(); i++) {
            TokenType op = mulExp.getOperators().get(i - 1).getType();
            right = buildUnaryExp(mulExp.getUnaryExps().get(i));
            if (right.getValueType().equals(IntegerType.i8)) {
                right = new ZextInst(IRData.getLocalVarName(currentFunction),
                        right, IntegerType.i32);
            }
            if (op == TokenType.MULT) {
                instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.MUL, left, right
                );
            } else if (op == TokenType.DIV) {
                instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.SDIV, left, right
                );
            } else if (op == TokenType.MOD) {
                instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.SREM, left, right
                );
            } else {
                throw new RuntimeException("Shouldn't reach here");
            }
            left = instruction;
        }
        return left;
    }

    private Value buildUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            return buildPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryOp() != null && unaryExp.getUnaryExp() != null) {
            Value left = buildUnaryExp(unaryExp.getUnaryExp());
            Value right = new ConstInt(IntegerType.i32, 0);
            TokenType type = unaryExp.getUnaryOp().getOperator().getType();
            if (type == TokenType.PLUS) {
                return left;
            } else if (type == TokenType.MINU) {
                return new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.SUB, right, left);
            } else if (type == TokenType.NOT) {
                Instruction instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.ICMP_EQ, right, left);
                return new ZextInst(IRData.getLocalVarName(currentFunction),
                        instruction, IntegerType.i32);
            } else {
                throw new RuntimeException("Shouldn't reach here");
            }
        } else if (unaryExp.getIdent() != null) {
            String funcName = unaryExp.getIdent().getContent();
            // 从根节点开始查找
            FuncSymbol funcSymbol = (FuncSymbol) TableManager.getInstance()
                    .getSymbol(funcName);
            Function function = funcSymbol.getLlvmValue();
            // 获取函数调用的实参
            ArrayList<Value> arguments = new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                arguments.addAll(buildFuncRParams(unaryExp.getFuncRParams()));
            }
            return new CallInst(IRData.getLocalVarName(currentFunction),
                    function, arguments);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private ArrayList<Value> buildFuncRParams(FuncRParams funcRParams) {
        ArrayList<Value> ans = new ArrayList<>();
        for (Exp exp : funcRParams.getExps()) {
            ans.add(buildExp(exp));
        }
        return ans;
    }

    private Value buildPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            return buildExp(primaryExp.getExp());
        } else if (primaryExp.getNumber() != null) {
            return new ConstInt(IntegerType.i32,
                    primaryExp.getNumber().getIntConstValue());
        } else if (primaryExp.getCharacter() != null) {
            return new ConstInt(IntegerType.i32,
                    primaryExp.getCharacter().getCharConstValue());
        } else if (primaryExp.getLVal() != null) {
            return buildLValValue(primaryExp.getLVal());
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    // 左值形式出现在等号右边时，或作为实参时
    private Value buildLValValue(LVal lVal) {
        ArrayList<Value> indexes = new ArrayList<>();
        if (lVal.getExp() != null) {
            indexes.add(buildExp(lVal.getExp()));
        }
        String name = lVal.getIdent().getContent();
        // 从符号表树的叶子节点向上查找
        VarSymbol varSymbol = (VarSymbol) currentTable.findSymbol(name);
        int dimension = varSymbol.getDimension();
        if (dimension == 0) {
            return new LoadInst(IRData.getLocalVarName(currentFunction),
                    varSymbol.getLlvmValue());
        } else if (dimension == 1) {
            if (indexes.isEmpty()) {
                return new GepInst(IRData.getLocalVarName(currentFunction),
                        varSymbol.getLlvmValue(), new ConstInt(IntegerType.i32, 0));
            } else {
                Instruction inst = new GepInst(IRData.getLocalVarName(currentFunction),
                        varSymbol.getLlvmValue(), indexes.get(0));
                return new LoadInst(IRData.getLocalVarName(currentFunction), inst);
            }
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    // 左值形式出现在等号左边
    private Value buildLValAssign(LVal lVal) {
        ArrayList<Value> indexes = new ArrayList<>();
        if (lVal.getExp() != null) {
            indexes.add(buildExp(lVal.getExp()));
        }
        String name = lVal.getIdent().getContent();
        // 从符号表树的叶子节点向上查找
        VarSymbol varSymbol = (VarSymbol) currentTable.findSymbol(name);
        int dimension = varSymbol.getDimension();
        if (dimension == 0) {
            return varSymbol.getLlvmValue();
        } else if (dimension == 1) {
            return new GepInst(IRData.getLocalVarName(currentFunction),
                    varSymbol.getLlvmValue(), indexes.get(0));
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private void buildFuncDef(FuncDef funcDef) {
        FuncSymbol funcSymbol = (FuncSymbol) currentTable.getSymbol(
                funcDef.getIdent().getContent());
        String name = "@" + funcDef.getIdent().getContent();
        ValueType funcReturnType = switch (funcSymbol.getType()) {
            case INT -> IntegerType.i32;
            case CHAR -> IntegerType.i8;
            case VOID -> IntegerType.VOID;
        };
        Function function = new Function(name, funcReturnType);
        funcSymbol.setLlvmValue(function);
        currentFunction = function;
        currentTable = currentTable.getChild();
        IRData.setCurFuncCnt(function);
        IRData.resetBasicBlockCnt();
        IRData.setCurrentBlock(new BasicBlock(IRData.getBasicBlockName(), function));
        if (funcDef.getFuncFParams() != null) {
            buildFuncFParams(funcDef.getFuncFParams());
        }
        buildBlock(funcDef.getBlock());
        BasicBlock lastBlock = IRData.getCurrentBlock();
        if (lastBlock.isEmpty() || !(lastBlock.getLastInstruction() instanceof RetInst)) {
            if (funcReturnType.equals(IntegerType.i32)
                    || funcReturnType.equals(IntegerType.i8)) {
                new RetInst(IRData.getLocalVarName(function),
                        new ConstInt(funcReturnType, 0));
            } else {
                new RetInst(IRData.getLocalVarName(function), null);
            }
        }
        currentTable = currentTable.getParent();
    }

    private void buildFuncFParams(FuncFParams funcFParams) {
        IRData.resetParamCnt();
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            buildFuncFParam(funcFParam);
        }
    }

    private void buildFuncFParam(FuncFParam funcFParam) {
        VarSymbol fParamSymbol = (VarSymbol) currentTable.getSymbol(
                funcFParam.getIdent().getContent());
        ValueType fParamType = switch (fParamSymbol.getType()) {
            case INT -> IntegerType.i32;
            case CHAR -> IntegerType.i8;
            default -> throw new RuntimeException("Shouldn't reach here");
        };
        if (fParamSymbol.getDimension() == 1) {
            fParamType = new PointerType(fParamType);
        }
        FuncParam funcParam = new FuncParam(IRData.getParamName(), fParamType);
        currentFunction.addFuncParam(funcParam);
        if (fParamType instanceof IntegerType integerType) {
            Instruction instruction = new AllocInst(
                    IRData.getLocalVarName(currentFunction),
                    integerType);
            fParamSymbol.setLlvmValue(instruction);
            new StoreInst(
                    IRData.getLocalVarName(currentFunction),
                    instruction, funcParam);
        } else {
            fParamSymbol.setLlvmValue(funcParam);
        }
    }

    private void buildMainFuncDef(MainFuncDef mainFuncDef) {
        FuncSymbol mainFuncSymbol = (FuncSymbol) currentTable.getSymbol("main");
        String name = "@main";
        ValueType funcReturnType = switch (mainFuncSymbol.getType()) {
            case INT -> IntegerType.i32;
            case CHAR -> IntegerType.i8;
            case VOID -> IntegerType.VOID;
        };
        Function mainFunction = new Function(name, funcReturnType);
        mainFuncSymbol.setLlvmValue(mainFunction);
        currentFunction = mainFunction;
        IRData.setCurFuncCnt(mainFunction);
        IRData.resetBasicBlockCnt();
        IRData.setCurrentBlock(new BasicBlock(IRData.getBasicBlockName(), mainFunction));
        currentTable = currentTable.getChild();
        buildBlock(mainFuncDef.getBlock());
        currentTable = currentTable.getParent();
    }

    private void buildBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            buildBlockItem(blockItem);
        }
    }

    private void buildBlockItem(BlockItem blockItem) {
        if (blockItem.getDecl() != null) {
            buildDecl(blockItem.getDecl());
        } else if (blockItem.getStmt() != null) {
            buildStmt(blockItem.getStmt());
        }
    }

    private void buildStmt(Stmt stmt) {
        if (stmt instanceof BlockStmt blockStmt) {
            currentTable = currentTable.getChild();
            buildBlock(blockStmt.getBlock());
            currentTable = currentTable.getParent();
        } else if (stmt instanceof ReturnStmt returnStmt) {
            buildReturnStmt(returnStmt);
        } else if (stmt instanceof LValExpStmt lValExpStmt) {
            buildAssign(lValExpStmt.getLVal(), lValExpStmt.getExp());
        } else if (stmt instanceof ExpStmt expStmt) {
            if (expStmt.getExp() != null) {
                buildExp(expStmt.getExp());
            }
        } else if (stmt instanceof IfStmt ifStmt) {
            buildIfStmt(ifStmt);
        } else if (stmt instanceof GetintStmt getintStmt) {
            buildGetintStmt(getintStmt);
        } else if (stmt instanceof GetcharStmt getcharStmt) {
            buildGetcharStmt(getcharStmt);
        } else if (stmt instanceof PrintfStmt printfStmt) {
            buildPrintfStmt(printfStmt);
        } else if (stmt instanceof ForStruct forStruct) {
            buildForStruct(forStruct);
        } else if (stmt instanceof BreakStmt) {
            buildBreakStmt();
        } else if (stmt instanceof ContinueStmt) {
            buildContinueStmt();
        }
    }

    private void buildAssign(LVal lVal, Exp exp) {
        Value lvalue = buildLValAssign(lVal);
        Value rvalue = buildExp(exp);
        ValueType targetType = ((PointerType) lvalue.getValueType()).getTargetType();
        if (rvalue.getValueType().equals(IntegerType.i32)
                && targetType.equals(IntegerType.i8)) {
            if (rvalue instanceof ConstInt constInt) {
                rvalue = new ConstInt(IntegerType.i8, constInt.getIntValue());
            } else {
                rvalue = new TruncInst(IRData.getLocalVarName(currentFunction),
                        rvalue, IntegerType.i8);
            }
        } else if (rvalue.getValueType().equals(IntegerType.i8)
                && targetType.equals(IntegerType.i32)) {
            if (rvalue instanceof ConstInt constInt) {
                rvalue = new ConstInt(IntegerType.i32, constInt.getIntValue());
            } else {
                rvalue = new ZextInst(IRData.getLocalVarName(currentFunction),
                        rvalue, IntegerType.i8);
            }
        }
        new StoreInst(IRData.getLocalVarName(currentFunction),
                lvalue, rvalue);
    }

    private void buildCond(Cond cond, BasicBlock trueBlock, BasicBlock falseBlock) {
        buildLOrExp(cond.getLOrExp(), trueBlock, falseBlock);
    }

    private void buildLOrExp(LOrExp lOrExp, BasicBlock trueBlock, BasicBlock falseBlock) {
        ArrayList<LAndExp> lAndExps = lOrExp.getlAndExps();
        for (int i = 0; i < lAndExps.size(); i++) {
            LAndExp lAndExp = lAndExps.get(i);
            if (i == lAndExps.size() - 1) {
                buildLAndExp(lAndExp, trueBlock, falseBlock);
            } else {
                BasicBlock nextBlock = new BasicBlock(IRData.getBasicBlockName(),
                        currentFunction);
                buildLAndExp(lAndExp, trueBlock, nextBlock);
                IRData.setCurrentBlock(nextBlock);
            }
        }
    }

    private void buildLAndExp(LAndExp lAndExp, BasicBlock trueBlock, BasicBlock falseBlock) {
        ArrayList<EqExp> eqExps = lAndExp.getEqExps();
        for (int i = 0; i < eqExps.size(); i++) {
            EqExp eqExp = eqExps.get(i);
            if (i == eqExps.size() - 1) {
                Value condition = buildEqExp(eqExp);
                new BrInst(IRData.getLocalVarName(currentFunction), condition, trueBlock,
                        falseBlock);
            } else {
                BasicBlock nextBlock = new BasicBlock(IRData.getBasicBlockName(),
                        currentFunction);
                Value condition = buildEqExp(eqExp);
                new BrInst(IRData.getLocalVarName(currentFunction), condition, nextBlock,
                        falseBlock);
                IRData.setCurrentBlock(nextBlock);
            }
        }
    }

    private Value buildEqExp(EqExp eqExp) {
        Value left = buildRelExp(eqExp.getRelExps().get(0));
        if (eqExp.getRelExps().size() == 1) {
            if (left.getValueType().equals(IntegerType.i32)) {
                left = new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ICMP_NE, left,
                        new ConstInt(IntegerType.i32, 0));
                return left;
            }
        }
        for (int i = 1; i < eqExp.getRelExps().size(); i++) {
            if (!left.getValueType().equals(IntegerType.i32)) {
                left = new ZextInst(IRData.getLocalVarName(currentFunction),
                        left, IntegerType.i32);
                Value right = buildRelExp(eqExp.getRelExps().get(i));
                if (!right.getValueType().equals(IntegerType.i32)) {
                    right = new ZextInst(IRData.getLocalVarName(currentFunction),
                            right, IntegerType.i32);
                }
                left = switch (eqExp.getOperators().get(i - 1).getType()) {
                    case EQL -> new BinaryInst(IRData.getLocalVarName(currentFunction),
                            OperatorType.ICMP_EQ, left, right);
                    case NEQ -> new BinaryInst(IRData.getLocalVarName(currentFunction),
                            OperatorType.ICMP_NE, left, right);
                    default -> throw new RuntimeException("Shouldn't reach here");
                };
            }
        }
        return left;
    }

    private Value buildRelExp(RelExp relExp) {
        Value left = buildAddExp(relExp.getAddExps().get(0));
        if (relExp.getAddExps().size() == 1) {
            return left;
        }
        for (int i = 1; i < relExp.getAddExps().size(); i++) {
            if (!left.getValueType().equals(IntegerType.i32)) {
                left = new ZextInst(IRData.getLocalVarName(currentFunction),
                        left, IntegerType.i32);
            }
            Value right = buildAddExp(relExp.getAddExps().get(i));
            left = switch (relExp.getOperators().get(i - 1).getType()) {
                case GRE -> new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ICMP_SGT, left, right);
                case GEQ -> new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ICMP_SGE, left, right);
                case LSS -> new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ICMP_SLT, left, right);
                case LEQ -> new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ICMP_SLE, left, right);
                default -> throw new RuntimeException("Shouldn't reach here");
            };
        }
        return left;
    }

    private void buildIfStmt(IfStmt ifStmt) {
        BasicBlock trueBlock = new BasicBlock(IRData.getBasicBlockName(),
                currentFunction);
        if (ifStmt.getStmt2() != null) {
            BasicBlock falseBlock = new BasicBlock(IRData.getBasicBlockName(),
                    currentFunction);
            BasicBlock followBlock = new BasicBlock(IRData.getBasicBlockName(),
                    currentFunction);
            buildCond(ifStmt.getCond(), trueBlock, falseBlock);
            IRData.setCurrentBlock(trueBlock);
            buildStmt(ifStmt.getStmt1());
            new BrInst(IRData.getLocalVarName(currentFunction), followBlock);
            IRData.setCurrentBlock(falseBlock);
            buildStmt(ifStmt.getStmt2());
            new BrInst(IRData.getLocalVarName(currentFunction), followBlock);
            IRData.setCurrentBlock(followBlock);
        } else {
            BasicBlock followBlock = new BasicBlock(IRData.getBasicBlockName(),
                    currentFunction);
            buildCond(ifStmt.getCond(), trueBlock, followBlock);
            IRData.setCurrentBlock(trueBlock);
            buildStmt(ifStmt.getStmt1());
            new BrInst(IRData.getLocalVarName(currentFunction), followBlock);
            IRData.setCurrentBlock(followBlock);
        }
    }

    private void buildReturnStmt(ReturnStmt returnStmt) {
        Value returnValue = null;
        if (returnStmt.getExp() != null) {
            returnValue = buildExp(returnStmt.getExp());
        } else if (currentFunction.getReturnType().equals(IntegerType.i8)) {
            returnValue = new ConstInt(IntegerType.i8, 0);
        } else if (currentFunction.getReturnType().equals(IntegerType.i32)) {
            returnValue = new ConstInt(IntegerType.i32, 0);
        }
        new RetInst(IRData.getLocalVarName(currentFunction), returnValue);
    }

    private void buildGetintStmt(GetintStmt getintStmt) {
        Value pointer = buildLValAssign(getintStmt.getLVal());
        Value storeValue = new GetintInst(IRData.getLocalVarName(currentFunction));
        ValueType targetType = ((PointerType) pointer.getValueType()).getTargetType();
        if (storeValue.getValueType().equals(IntegerType.i32)
                && targetType.equals(IntegerType.i8)) {
            storeValue = new TruncInst(IRData.getLocalVarName(currentFunction),
                    storeValue, IntegerType.i8);
        }
        new StoreInst(IRData.getLocalVarName(currentFunction),
                pointer, storeValue);
    }

    private void buildGetcharStmt(GetcharStmt getcharStmt) {
        Value pointer = buildLValAssign(getcharStmt.getLVal());
        Value storeValue = new GetcharInst(IRData.getLocalVarName(currentFunction));
        ValueType targetType = ((PointerType) pointer.getValueType()).getTargetType();
        if (storeValue.getValueType().equals(IntegerType.i32)
                && targetType.equals(IntegerType.i8)) {
            storeValue = new TruncInst(IRData.getLocalVarName(currentFunction),
                    storeValue, IntegerType.i8);
        }
        new StoreInst(IRData.getLocalVarName(currentFunction),
                pointer, storeValue);
    }

    private void buildPrintfStmt(PrintfStmt printfStmt) {
        ArrayList<Value> values = new ArrayList<>();
        for (Exp exp : printfStmt.getExps()) {
            values.add(buildExp(exp));
        }
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
            ConstString constString = new ConstString(
                    IRData.getConstStringName(), tempString);
            new PutstrInst(IRData.getLocalVarName(currentFunction), constString);
            if (typeString.equals("%d")) {
                Value value = values.get(cnt++);
                if (!value.getValueType().equals(IntegerType.i32)) {
                    if (value instanceof ConstInt constInt) {
                        value = new ConstInt(IntegerType.i32, constInt.getIntValue());
                    } else {
                        value = new ZextInst(IRData.getLocalVarName(currentFunction),
                                value, IntegerType.i32);
                    }
                }
                new PutintInst(IRData.getLocalVarName(currentFunction), value);
            } else if (typeString.equals("%c")) {
                Value value = values.get(cnt++);
                if (value.getValueType().equals(IntegerType.i32)) {
                    if (value instanceof ConstInt constInt) {
                        value = new ConstInt(IntegerType.i8, constInt.getIntValue());
                    } else {
                        value = new TruncInst(IRData.getLocalVarName(currentFunction),
                                value, IntegerType.i8);
                    }
                }
                new PutchInst(IRData.getLocalVarName(currentFunction), value);
            }
            pos = start + 2;
        }
        if (pos < formatString.length()) {
            String tempString = formatString.substring(pos);
            ConstString constString = new ConstString(
                    IRData.getConstStringName(), tempString);
            new PutstrInst(IRData.getLocalVarName(currentFunction), constString);
        }
    }

    private void buildForStruct(ForStruct forStruct) {
        // forStmt和LValAssignExp本质上是一样的
        if (forStruct.getForStmt1() != null) {
            buildAssign(forStruct.getForStmt1().getLVal(),
                    forStruct.getForStmt1().getExp());
        }
        BasicBlock conditionBlock = new BasicBlock(IRData.getBasicBlockName(),
                currentFunction);
        BasicBlock bodyBlock = new BasicBlock(IRData.getBasicBlockName(),
                currentFunction);
        BasicBlock followBlock = new BasicBlock(IRData.getBasicBlockName(),
                currentFunction);
        IRData.push(new ForLoop(conditionBlock, bodyBlock, followBlock));
        new BrInst(IRData.getLocalVarName(currentFunction), conditionBlock);
        IRData.setCurrentBlock(conditionBlock);
        if (forStruct.getCond() != null) {
            buildCond(forStruct.getCond(), bodyBlock, followBlock);
        } else {
            new BrInst(IRData.getLocalVarName(currentFunction), bodyBlock);
        }
        IRData.setCurrentBlock(bodyBlock);
        buildStmt(forStruct.getStmt());
        // 循环量更新直接嵌入bodyBlock
        if (forStruct.getForStmt2() != null) {
            buildAssign(forStruct.getForStmt2().getLVal(),
                    forStruct.getForStmt2().getExp());
        }
        new BrInst(IRData.getLocalVarName(currentFunction), conditionBlock);
        IRData.setCurrentBlock(followBlock);
        IRData.pop();
    }

    private void buildBreakStmt() {
        new BrInst(IRData.getLocalVarName(currentFunction),
                IRData.peek().getFollowBlock());
    }

    private void buildContinueStmt() {
        new BrInst(IRData.getLocalVarName(currentFunction),
                IRData.peek().getConditionBlock());
    }
}