package middle.component;

import middle.component.model.Value;
import middle.component.types.ValueType;

public class Argument extends Value {
    public Argument(ValueType valueType) {
        super("%" + Value.allocIdCount(), valueType);
    }

    @Override
    public String toString() {
        return getValueType().toString() + " " + getName();
    }
}