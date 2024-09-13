package IR;

import IO.OutputHandler;
import IR.values.Function;
import IR.values.GlobalVar;

import java.util.ArrayList;

public class IRModule {
    private static final IRModule irModule = new IRModule();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    public static IRModule getInstance() {
        return irModule;
    }

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

    public void genLLVm() {
        String libFunc = """
                declare i32 @getint()
                declare void @putint(i32)
                declare void @putch(i32)
                declare void @putstr(i8*)""";
        OutputHandler.genLLVM(libFunc);
        for (GlobalVar globalVar : globalVars) {
            OutputHandler.genLLVM(globalVar.toString());
        }
        for (Function function : functions) {
            function.genLLVM();
        }
    }
}
