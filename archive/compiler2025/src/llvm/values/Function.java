package llvm.values;

import llvm.IRModule;
import llvm.types.FuncType;
import llvm.types.Type;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Function extends Value {
    private final boolean isBuiltIn;
    private final ArrayList<Argument> arguments;
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    public Function(String name, Type type, boolean isBuiltIn) {
        super(name, type);
        valueIdCount = 0;
        this.isBuiltIn = isBuiltIn;
        this.arguments = new ArrayList<>();
        addAllArguments();
        if (!isBuiltIn) {
            IRModule.getInstance().addFunction(this);
        }

    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }

    public void addAllArguments() {
        for (Type type : ((FuncType) getType()).getParamsType()) {
            this.arguments.add(new Argument(type));
        }
    }

    public void refreshName() {
        valueIdCount = 0;
        for (Argument argument : arguments) {
            argument.refreshName();
        }
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.refreshName();
        }
    }

    public Argument getArgument(int i) {
        return arguments.get(i);
    }

    public void toLLVM() {
        String returnType = ((FuncType) getType()).getReturnType().toString();
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
