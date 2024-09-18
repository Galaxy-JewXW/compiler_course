package middle.component;

import middle.Module;
import middle.component.model.Value;
import middle.component.types.FunctionType;
import middle.component.types.ValueType;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Function extends Value {
    private final ArrayList<Argument> arguments = new ArrayList<>();
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private final boolean isBuiltIn;

    public Function(String name, ValueType valueType, boolean isBuiltIn) {
        super(name, valueType);
        valueIdCount = 0;
        this.isBuiltIn = isBuiltIn;
        for (ValueType type : ((FunctionType) getValueType()).getParametersTypes()) {
            this.arguments.add(new Argument(type));
        }
        if (!isBuiltIn) {
            Module.getInstance().addFunction(this);
        }
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public void toLLVM() {
        String returnType = ((FunctionType) getValueType()).getReturnType().toString();
        StringJoiner sj = new StringJoiner(", ");
        for (Argument argument : arguments) {
            sj.add(argument.toString());
        }
        System.out.println("define dso_local " + returnType
                + " @" + getName() + "(" + sj + ") {");
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.toLLVM();
        }
        System.out.println("}");
    }
}
