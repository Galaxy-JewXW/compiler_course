package llvm.values;

import llvm.IRModule;
import llvm.types.PointerType;
import llvm.types.Type;

public class GlobalVar extends User {
    private final boolean isConstant;
    private final Value value;

    public GlobalVar(String name, Type type, boolean isConstant, Value value) {
        super("@" + name, new PointerType(type));
        this.isConstant = isConstant;
        this.value = value;
        IRModule.getInstance().addGlobalVar(this);
    }

    public Value getValue() {
        return value;
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String toString() {
        return getName() + " = dso_local " +
                (isConstant ? "constant" : "global") + " " +
                value.toString();
    }
}
