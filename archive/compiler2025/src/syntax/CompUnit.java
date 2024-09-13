package syntax;

import error.ErrorVisitor;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

// 编译单元 CompUnit -> {Decl} {FuncDef} MainFuncDef
public class CompUnit {
    private final ArrayList<Decl> decls;
    private final ArrayList<FuncDef> funcDefs;
    private final MainFuncDef mainFuncDef;

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

    public void output(String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        for (Decl decl : decls) {
            decl.output();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.output();
        }
        mainFuncDef.output();
        System.out.println("<CompUnit>");
        System.setOut(origin);
    }

    public void check() {
        ErrorVisitor.getInstance().addTable(null);
        for (Decl decl : decls) {
            decl.check();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.check();
        }
        mainFuncDef.check();
    }
}
