package IR.values;

import IR.IRModule;
import IR.types.PointerType;
import IR.types.Type;

public class GlobalVar extends User {
    private final boolean isConst;
    private final Value value;

    public GlobalVar(String name, Type type, boolean isConst, Value value) {
        super("@" + name, new PointerType(type));
        this.isConst = isConst;
        this.value = value;
        IRModule.getInstance().addGlobalVar(this);
    }

    public Value getValue() {
        return value;
    }

    public boolean isConst() {
        return isConst;
    }

    @Override
    public String toString() {
        if (isConst) {
            return String.format("%s = dso_local constant %s", getName(), value);
        } else {
            return String.format("%s = dso_local global %s", getName(), value);
        }
    }
}
