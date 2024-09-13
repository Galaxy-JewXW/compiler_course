package llvm.values;

import llvm.IRModule;
import llvm.types.FunctionType;
import llvm.types.ValueType;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Function extends Value {
    private final boolean isBuiltIn;
    private final ArrayList<Argument> arguments = new ArrayList<>();
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    public Function(String name, ValueType type, boolean isBuiltIn) {
        super(name, type);
        valueCnt = 0;
        addAllArgs();
        this.isBuiltIn = isBuiltIn;
        if (!isBuiltIn) {
            IRModule.getInstance().addFunction(this);
        }
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }

    public void addAllArgs() {
        for (ValueType type : ((FunctionType)getType()).getParamsType()) {
            arguments.add(new Argument(type));
        }
    }

    public Argument getArgument(int p) {
        return arguments.get(p);
    }

    public void toLLVM() {
        String returnType = ((FunctionType) getType()).getReturnType().toString();
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
