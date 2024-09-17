package middle;

import frontend.syntax.CompUnit;
import frontend.syntax.Decl;
import frontend.syntax.function.FuncDef;
import frontend.syntax.function.MainFuncDef;
import frontend.syntax.variable.ConstDecl;
import frontend.syntax.variable.ConstDef;
import frontend.syntax.variable.ConstInitVal;
import frontend.syntax.variable.VarDecl;
import frontend.token.TokenType;
import middle.model.Value;
import middle.types.IntegerType;
import middle.types.PointerType;
import middle.types.ValueType;
import middle.types.VoidType;
import tools.InstBuilder;

import java.util.ArrayList;

public class IRVisitor {
    private final SymbolTable symbolTable = new SymbolTable();
    private final CompUnit compUnit;
    private Function curFunction;
    private BasicBlock curBlock;
    private BasicBlock curTrueBlock;
    private BasicBlock curFalseBlock;
    private BasicBlock curEndBlock;
    private BasicBlock curForEndBlock;
    private int immediate;
    private Value tempValue;
    private ValueType tempValueType;
    private boolean isGlobal;
    private boolean isCalculable;

    public IRVisitor(CompUnit root) {
        this.compUnit = root;
    }

    public void build() {
        visitCompUnit();
    }

    private void visitCompUnit() {
        symbolTable.addTable();
        symbolTable.addSymbol("getint", InstBuilder.buildBuiltInFunc("getint",
                IntegerType.i32, new ArrayList<>()));
        symbolTable.addSymbol("getchar", InstBuilder.buildBuiltInFunc("getint",
                IntegerType.i32, new ArrayList<>()));
        symbolTable.addSymbol("putint", InstBuilder.buildBuiltInFunc("putint",
                VoidType.VOID, IntegerType.i32));
        symbolTable.addSymbol("putch", InstBuilder.buildBuiltInFunc("putch",
                VoidType.VOID, IntegerType.i32));
        symbolTable.addSymbol("putstr", InstBuilder.buildBuiltInFunc("putstr",
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
            visitConstInitVal(constDef.getConstInitVal());
            symbolTable.addConst(name, immediate);
            ConstInt constInt = InstBuilder.buildConstInt(immediate, tempValueType);
        }
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {

    }

    private void visitVarDecl(VarDecl varDecl) {

    }

    private void visitFuncDef(FuncDef funcDef) {

    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {

    }

}
