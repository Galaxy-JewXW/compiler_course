package llvm;

import llvm.values.Function;
import llvm.values.GlobalVar;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class IRModule {
    private static final IRModule irModule = new IRModule();

    public static IRModule getInstance() {
        return irModule;
    }

    private IRModule() {}

    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ArrayList<GlobalVar> getGlobalVars() {
        return globalVars;
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
        for (Function function : functions) {
            function.toLLVM();
        }
        System.setOut(origin);
    }
}
