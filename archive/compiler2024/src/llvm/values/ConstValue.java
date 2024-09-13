package llvm.values;

import llvm.types.ValueType;

public class ConstValue extends Value implements Assignable {
    public ConstValue(String name, ValueType type) {
        super(name, type);
    }
}
