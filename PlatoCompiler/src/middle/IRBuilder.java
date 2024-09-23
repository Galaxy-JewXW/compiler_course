package middle;

import frontend.SymbolTable;
import frontend.TableManager;
import frontend.symbol.VarSymbol;
import frontend.syntax.CompUnit;
import frontend.syntax.Decl;
import frontend.syntax.function.FuncDef;
import frontend.syntax.function.MainFuncDef;
import frontend.syntax.variable.ConstDecl;
import frontend.syntax.variable.ConstDef;
import frontend.syntax.variable.VarDecl;
import frontend.syntax.variable.VarDef;
import middle.component.GlobalVar;
import middle.component.InitialValue;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

public class IRBuilder {
    private final SymbolTable rootTable = TableManager.getInstance()
            .getCurrentTable();
    private SymbolTable currentTable = rootTable;
    private final CompUnit compUnit;
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
        }
    }

    private void buildVarDecl(VarDecl varDecl) {

    }

    private void buildVarDef(VarDef varDef) {

    }

    private void buildFuncDef(FuncDef funcDef) {

    }

    private void buildMainFuncDef(MainFuncDef mainFuncDef) {

    }
}