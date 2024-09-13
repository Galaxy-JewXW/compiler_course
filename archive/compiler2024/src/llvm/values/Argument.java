package llvm.values;

import llvm.types.ValueType;

public class Argument extends Value {
    public Argument(ValueType type) {
        super("%" + valueCnt, type);
        valueCnt++;
    }

    @Override
    public String toString() {
        return getType().toString() + " " + getName();
    }
}
