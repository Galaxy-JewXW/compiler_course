package middle.component;

import middle.component.model.User;
import middle.component.type.ValueType;

public class GlobalVar extends User {
    private final InitialValue initialValue;

    public GlobalVar(String name, ValueType type, InitialValue initialValue) {
        super(name, type);
        this.initialValue = initialValue;
    }

    @Override
    public String toString() {
        return getName() + " = dso_local global " + initialValue;
    }
}
