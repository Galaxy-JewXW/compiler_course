package middle.component;

import middle.component.model.Value;
import middle.component.type.ValueType;

public class ConstInt extends Value {
    private final int intValue;

    public ConstInt(ValueType valueType, int intValue) {
        super(String.valueOf(intValue), valueType);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
