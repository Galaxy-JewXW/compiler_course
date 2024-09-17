package middle.types;

import middle.model.Value;

public class Argument extends Value {
    public Argument(ValueType valueType) {
        super("%" + valueIdCount++, valueType);
    }

    @Override
    public String toString() {
        return getValueType().toString() + " " + getName();
    }
}
