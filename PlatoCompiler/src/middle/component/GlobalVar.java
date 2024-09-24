package middle.component;

import middle.component.model.User;
import middle.component.type.ValueType;

public class GlobalVar extends User {
    private final InitialValue initialValue;
    private final boolean isConstant;

    public GlobalVar(String name, ValueType type, InitialValue initialValue, boolean isConstant) {
        super(name, type);
        this.initialValue = initialValue;
        this.isConstant = isConstant;
        Module.getInstance().addGlobalVar(this);
    }

    public InitialValue getInitialValue() {
        return initialValue;
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String toString() {
        return getName() + " = dso_local global " + initialValue;
    }
}
