package middle;

import middle.model.User;
import middle.model.Value;
import middle.types.PointerType;
import middle.types.ValueType;

public class GlobalVar extends User {
    private final Value value;
    private final boolean isConstant;

    public GlobalVar(String name, ValueType valueType, Value value, boolean isConstant) {
        super("@" + name, new PointerType(valueType));
        this.value = value;
        this.isConstant = isConstant;
        Module.getInstance().addGlobalVar(this);
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getName() + " = dso_local " +
                (isConstant ? "constant " : "global ") +
                value.toString();
    }
}
