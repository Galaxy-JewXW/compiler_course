package middle;

import middle.component.Function;
import middle.component.GlobalVar;

import java.util.ArrayList;

public class Module {
    private static final Module IR_MODULE = new Module();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    public static Module getInstance() {
        return IR_MODULE;
    }

    private Module() {
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
}
