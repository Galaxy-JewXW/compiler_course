package middle;

import middle.types.IntegerType;
import middle.types.ValueType;

public class ConstInt extends ConstVar {
    private final int intValue;

    public ConstInt(int intValue, ValueType intType) {
        super(String.valueOf(intValue), intType);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return getValueType().toString() + " " + intValue;
    }

    public static final ConstInt i32ZERO = new ConstInt(0, IntegerType.i32);
}
