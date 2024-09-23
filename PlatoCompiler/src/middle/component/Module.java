package middle.component;

import middle.component.model.Value;
import middle.component.type.LabelType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Module extends Value {
    private final ArrayList<String> builtInFunctions = new ArrayList<>();
    private final ArrayList<ConstString> constStrings = new ArrayList<>();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    // 一个文件只有一个module，可以使用单例模式
    private static final Module INSTANCE = new Module();

    public static Module getInstance() {
        return INSTANCE;
    }

    private Module() {
        super("zxw", new LabelType());
        builtInFunctions.add("declare i32 @getint()");
        builtInFunctions.add("declare i32 @getchar()");
        builtInFunctions.add("declare void @putint(i32)");
        builtInFunctions.add("declare void @putch(i8)");
        builtInFunctions.add("declare void @putstr(i8*)");
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void addConstString(ConstString constString) {
        constStrings.add(constString);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    @Override
    public String toString() {
        return String.join("\n", builtInFunctions) +
                constStrings.stream().map(Object::toString)
                        .collect(Collectors.joining("\n")) + "\n\n" +
                globalVars.stream().map(GlobalVar::toString)
                        .collect(Collectors.joining("\n")) + "\n\n" +
                functions.stream().map(Function::toString)
                        .collect(Collectors.joining("\n\n"));
    }
}
