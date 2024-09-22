package llvm;

import llvm.model.Value;
import llvm.type.ValueType;

public class ConstInt extends Value {
    private int intValue;

    public ConstInt(ValueType valueType, int intValue) {
        super(String.valueOf(intValue), valueType);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
