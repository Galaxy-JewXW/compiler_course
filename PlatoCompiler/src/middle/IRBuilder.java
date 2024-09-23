package middle;

import frontend.SymbolTable;
import frontend.TableManager;
import frontend.symbol.FuncSymbol;
import frontend.symbol.VarSymbol;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.*;
import frontend.syntax.statement.BlockStmt;
import frontend.syntax.statement.Stmt;
import frontend.syntax.variable.*;
import frontend.token.TokenType;
import middle.component.*;
import middle.component.Module;
import middle.component.instruction.*;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;
import tools.StrToArray;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRBuilder {
    private final SymbolTable rootTable = TableManager.getInstance()
            .getCurrentTable();
    private SymbolTable currentTable = rootTable;
    private final CompUnit compUnit;
    private final Module module = Module.getInstance();
    private Function currentFunction = null;
    private BasicBlock currentBlock = null;
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
                    currentFunction), initialValue.getValueType(), currentBlock);
            varSymbol.setLlvmValue(instruction);
            if (varSymbol.getDimension() == 0) {
                int init = initialValue.getElements().get(0);
                new StoreInst(IRData.getLocalVarName(currentFunction),
                        instruction, new ConstInt(initialValue.getValueType(), init),
                        currentBlock);
            } else if (varSymbol.getDimension() == 1) {
                Value pointer = instruction;
                for (int i = 0; i < initialValue.getElements().size(); i++) {
                    instruction = new GepInst(IRData.getLocalVarName(currentFunction),
                            pointer,
                            new ConstInt(IntegerType.i32, i),
                            currentBlock
                    );
                    new StoreInst(IRData.getLocalVarName(currentFunction),
                            instruction,
                            new ConstInt(IntegerType.i32, initialValue.getElements().get(i)),
                            currentBlock);
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
            Instruction instruction;
            ValueType valueType = switch (varSymbol.getType()) {
                case INT -> IntegerType.i32;
                case CHAR -> IntegerType.i8;
                default -> throw new RuntimeException("Shouldn't reach here");
            };
            if (varSymbol.getDimension() == 0) {
                instruction = new AllocInst(IRData.getLocalVarName(
                        currentFunction),
                        valueType, currentBlock);
                varSymbol.setLlvmValue(instruction);
                if (varDef.getInitVal() != null) {
                    ArrayList<Value> inits = buildInitVal(varDef.getInitVal());
                    new StoreInst(IRData.getLocalVarName(currentFunction), instruction,
                            inits.get(0), currentBlock);
                }
            } else {
                valueType = new ArrayType(varSymbol.getLength(), valueType);
                instruction = new AllocInst(IRData.getLocalVarName(
                        currentFunction),
                        valueType, currentBlock);
                varSymbol.setLlvmValue(instruction);
                if (varDef.getInitVal() != null) {
                    ArrayList<Value> inits = buildInitVal(varDef.getInitVal());
                    for (int i = 0; i < inits.size(); i++) {
                        Instruction inst = new GepInst(IRData.getLocalVarName(currentFunction), instruction,
                                new ConstInt(IntegerType.i32, i), currentBlock);
                        new StoreInst(IRData.getLocalVarName(currentFunction), inst,
                                inits.get(i), currentBlock);
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
        Value right;
        Instruction instruction;
        for (int i = 1; i < addExp.getMulExps().size(); i++) {
            TokenType op = addExp.getOperators().get(i - 1).getType();
            right = buildMulExp(addExp.getMulExps().get(i));
            if (op == TokenType.PLUS) {
                instruction = new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.ADD, left, right, currentBlock
                );
            } else {
                instruction = new BinaryInst(IRData.getLocalVarName(currentFunction),
                        OperatorType.SUB, left, right, currentBlock
                );
            }
            left = instruction;
        }
        return left;
    }

    private Value buildMulExp(MulExp mulExp) {
        Value left = buildUnaryExp(mulExp.getUnaryExps().get(0));
        Value right;
        Instruction instruction;
        for (int i = 1; i < mulExp.getUnaryExps().size(); i++) {
            TokenType op = mulExp.getOperators().get(i - 1).getType();
            right = buildUnaryExp(mulExp.getUnaryExps().get(i));
            if (op == TokenType.MULT) {
                instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.MUL, left, right, currentBlock
                );
            } else if (op == TokenType.DIV) {
                instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.SDIV, left, right, currentBlock
                );
            } else if (op == TokenType.MOD) {
                instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.SREM, left, right, currentBlock
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
                        OperatorType.SUB, right, left, currentBlock);
            } else if (type == TokenType.NOT) {
                Instruction instruction = new BinaryInst(IRData.getLocalVarName(
                        currentFunction),
                        OperatorType.ICMP_EQ, right, left, currentBlock);
                return new ZextInst(IRData.getLocalVarName(currentFunction),
                        instruction, IntegerType.i32, currentBlock);
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
                    function, arguments, currentBlock);
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
            return new ConstInt(IntegerType.i8,
                    primaryExp.getCharacter().getCharConstValue());
        } else if (primaryExp.getLVal() != null) {
            return buildLValValue(primaryExp.getLVal());
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    // 左值形式出现在等号右边时
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
                    varSymbol.getLlvmValue(), currentBlock);
        } else if (dimension == 1) {
            if (indexes.isEmpty()) {
                return new GepInst(IRData.getLocalVarName(currentFunction),
                        varSymbol.getLlvmValue(), new ConstInt(IntegerType.i32, 0),
                        currentBlock);
            } else {
                Instruction inst = new GepInst(IRData.getLocalVarName(currentFunction),
                        varSymbol.getLlvmValue(), indexes.get(0),
                        currentBlock);
                return new LoadInst(IRData.getLocalVarName(currentFunction),
                        inst, currentBlock);
            }
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
        IRData.setCurrentFunction(function);
        IRData.resetBasicBlockCnt();
        currentBlock = new BasicBlock(IRData.getBasicBlockName(), function);
        if (funcDef.getFuncFParams() != null) {
            buildFuncFParams(funcDef.getFuncFParams());
        }
        buildBlock(funcDef.getBlock());
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
                    integerType, currentBlock);
            fParamSymbol.setLlvmValue(instruction);
            new StoreInst(
                    IRData.getLocalVarName(currentFunction),
                    instruction, funcParam, currentBlock
            );
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
        IRData.setCurrentFunction(mainFunction);
        IRData.resetBasicBlockCnt();
        currentBlock = new BasicBlock(IRData.getBasicBlockName(), mainFunction);
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
        }
    }
}