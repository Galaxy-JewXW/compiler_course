package llvm;

import llvm.values.BasicBlock;
import llvm.values.Function;
import llvm.values.GlobalVar;
import llvm.values.instructions.Instruction;
import llvm.values.instructions.ZextInst;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class IRModule {
    private static final IRModule IR_MODULE = new IRModule();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    public static IRModule getInstance() {
        return IR_MODULE;
    }

    private IRModule() {}

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }

    public ArrayList<GlobalVar> getGlobalVars() {
        return globalVars;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void refreshName() {
        for (Function function : functions) {
            function.refreshName();
        }
    }

    public void toLLVM(String path) throws FileNotFoundException {


        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        String builtInFuncs = """
                declare i32 @getint()
                declare void @putint(i32)
                declare void @putch(i32)
                declare void @putstr(i8*)""";
        System.out.println(builtInFuncs);
        for (GlobalVar globalVar : globalVars) {
            System.out.println(globalVar);
        }
        System.out.print("\n");
        for (Function function : functions) {
            function.toLLVM();
            System.out.print("\n");
        }
        System.setOut(origin);
    }
}
