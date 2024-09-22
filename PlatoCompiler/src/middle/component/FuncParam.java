package middle.component;

import middle.component.model.Value;
import middle.component.type.ValueType;

public class FuncParam extends Value {
    public FuncParam(String name, ValueType valueType) {
        super(name, valueType);
    }

    @Override
    public String toString() {
        return getValueType() + " " + getName();
    }
}
