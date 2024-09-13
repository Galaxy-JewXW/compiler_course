package node;

import IO.OutputHandler;
import error.ErrorHandler;
import node.decl.Decl;
import node.func.FuncDef;
import node.func.MainFuncDef;

import java.util.ArrayList;

public class CompUnit {
    //CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }

    public ArrayList<Decl> getDecls() {
        return decls;
    }

    public ArrayList<FuncDef> getFuncDefs() {
        return funcDefs;
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }

    public void print() {
        for (Decl decl : decls) {
            decl.print();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.print();
        }
        mainFuncDef.print();
        OutputHandler.println("<CompUnit>");
    }

    public void checkError() {
        ErrorHandler.getInstance().addSymbolTable(null);
        for (Decl decl : decls) {
            decl.checkError();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.checkError();
        }
        mainFuncDef.checkError();
    }
}
