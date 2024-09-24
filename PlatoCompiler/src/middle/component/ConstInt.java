package middle.component;

import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

public class ConstInt extends Value {
    private final int intValue;

    private static String setName(ValueType valueType, int intValue) {
        if (valueType.equals(IntegerType.i8)) {
            return String.valueOf(intValue & 0xFF);
        } else {
            return String.valueOf(intValue);
        }
    }


    public ConstInt(ValueType valueType, int intValue) {
        super(setName(valueType, intValue), valueType);
        if (valueType.equals(IntegerType.i8)) {
            this.intValue = intValue & 0xFF;
        } else {
            this.intValue = intValue;
        }
    }

    public int getIntValue() {
        return intValue;
    }


}
