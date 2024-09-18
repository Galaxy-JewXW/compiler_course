package middle;

import frontend.syntax.Character;
import frontend.syntax.Number;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.FuncDef;
import frontend.syntax.function.MainFuncDef;
import frontend.syntax.statement.BlockStmt;
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
                tempValue = new ConstInt(immediate, tempValueType);
            }
        } else if (constInitVal.getStringConst() != null) {
            String stringConst = constInitVal.getStringConst().getContent();
            ConstArray constArray = new ConstArray(length);
            for (int i = 1; i < stringConst.length() - 1; i++) {
                constArray.addElement(new ConstInt(stringConst.charAt(i), IntegerType.i8));
            }
            constArray.setFilled();
            int unfilled = length - (stringConst.length() - 2);
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(new ConstInt(0, IntegerType.i8));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i8, length);
        } else if (constInitVal.getConstExps() != null) {
            ConstArray constArray = new ConstArray(length);
            for (ConstExp constExp : constInitVal.getConstExps()) {
                visitConstExp(constExp);
                constArray.addElement(new ConstInt(immediate, IntegerType.i32));
            }
            constArray.setFilled();
            int unfilled = length - constInitVal.getConstExps().size();
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(new ConstInt(0, IntegerType.i32));
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
                constArray.addElement(new ConstInt(stringConst.charAt(i), IntegerType.i8));
            }
            constArray.setFilled();
            int unfilled = length - (stringConst.length() - 2);
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(new ConstInt(0, IntegerType.i8));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i8, length);
        } else if (initVal.getExps() != null) {
            ConstArray constArray = new ConstArray(length);
            for (Exp exp : initVal.getExps()) {
                visitExp(exp);
                if (isGlobal) {
                    tempValue = new ConstInt(immediate, IntegerType.i32);
                }
                constArray.addElement(tempValue);
            }
            constArray.setFilled();
            int unfilled = length - initVal.getExps().size();
            for (int i = 0; i < unfilled; i++) {
                constArray.addElement(new ConstInt(0, IntegerType.i32));
            }
            constArray.resetType();
            tempValue = constArray;
            tempValueType = new ArrayType(IntegerType.i32, length);
        } else {
            throw new RuntimeException("Shouldn't reach here");
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
        if (stmt instanceof BlockStmt blockStmt) {
            symbolTable.addTable();
            visitBlock(blockStmt.getBlock());
            symbolTable.removeTable();
        }
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
