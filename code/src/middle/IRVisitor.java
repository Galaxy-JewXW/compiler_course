package middle;

import frontend.syntax.Character;
import frontend.syntax.Number;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.FuncDef;
import frontend.syntax.function.MainFuncDef;
import frontend.syntax.statement.Stmt;
import frontend.syntax.variable.*;
import frontend.token.TokenType;
import middle.instructions.OperatorType;
import middle.model.Value;
import middle.types.*;
import tools.Builder;

import java.util.ArrayList;

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
        symbolTable.addSymbol("getchar", Builder.buildBuiltInFunc("getint",
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
            visitConstInitVal(constDef.getConstInitVal());
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
            // 对应一维数组(int, char)的定义
        }
        isCalculable = false;
        symbolTable.addSymbol(name, tempValue);
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() != null) {
            // 对应'const int a = 3;'
            visitConstExp(constInitVal.getConstExp());
            if (isGlobal || isCalculable) {
                tempValue = new ConstInt(immediate, tempValueType);
            }
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
                visitInitVal(varDef.getInitVal());
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
        }
        symbolTable.addSymbol(name, tempValue);
    }

    private void visitInitVal(InitVal initVal) {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp());
        }
    }

    private void visitFuncDef(FuncDef funcDef) {

    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        FunctionType functionType = Builder.buildFunctionType(IntegerType.i32, new ArrayList<>());
        Function function = Builder.buildFunction("main", functionType);
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

    }

    private void visitExp(Exp exp) {
        if (exp != null) {
            visitAddExp(exp.getAddExp());
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else if (primaryExp.getCharacter() != null) {
            visitCharacter(primaryExp.getCharacter());
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
                tempValue = Builder.buildBinaryInst(curValue, op, tempValue, curBlock);
            }
        }
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }

}
