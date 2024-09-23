package middle;

import frontend.SymbolTable;
import frontend.TableManager;
import frontend.symbol.FuncSymbol;
import frontend.symbol.VarSymbol;
import frontend.syntax.Block;
import frontend.syntax.CompUnit;
import frontend.syntax.Decl;
import frontend.syntax.function.FuncDef;
import frontend.syntax.function.FuncFParam;
import frontend.syntax.function.FuncFParams;
import frontend.syntax.function.MainFuncDef;
import frontend.syntax.variable.ConstDecl;
import frontend.syntax.variable.ConstDef;
import frontend.syntax.variable.VarDecl;
import frontend.syntax.variable.VarDef;
import middle.component.*;
import middle.component.Module;
import middle.component.instruction.AllocInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.StoreInst;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

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
            Instruction instruction = new AllocInst(IRData.getLocalVarName(currentFunction, true),
                    initialValue.getValueType(), currentBlock);
            varSymbol.setLlvmValue(instruction);
            if (varSymbol.getDimension() == 0) {
                int init = initialValue.getElements().get(0);
                instruction = new StoreInst(IRData.getLocalVarName(currentFunction, false),
                        instruction, new ConstInt(initialValue.getValueType(), init),
                        currentBlock);
            } else if (varSymbol.getDimension() == 1) {
                Value pointer = instruction;
                for (int i = 0; i < initialValue.getElements().size(); i++) {
                    instruction = new GepInst(IRData.getLocalVarName(currentFunction, true),
                            pointer,
                            new ConstInt(IntegerType.i32, i),
                            currentBlock
                    );
                    instruction = new StoreInst(IRData.getLocalVarName(currentFunction, false),
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

    }

    private void buildVarDef(VarDef varDef) {

    }

    private void buildFuncDef(FuncDef funcDef) {
        FuncSymbol funcSymbol = (FuncSymbol) currentTable.getSymbol(funcDef.getIdent().getContent());
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
                    IRData.getLocalVarName(currentFunction, true),
                    integerType, currentBlock);
            fParamSymbol.setLlvmValue(instruction);
            instruction = new StoreInst(
                    IRData.getLocalVarName(currentFunction, false),
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
    }

    private void buildBlock(Block block) {

    }
}