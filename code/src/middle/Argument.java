package middle;

import middle.model.Value;
import middle.types.ValueType;

public class Argument extends Value {
    public Argument(ValueType valueType) {
        super("%" + valueIdCount++, valueType);
    }

    @Override
    public String toString() {
        return getValueType().toString() + " " + getName();
    }
}
