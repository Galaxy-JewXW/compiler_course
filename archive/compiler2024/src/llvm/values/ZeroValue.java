package llvm.values;

import llvm.types.IntType;

public class ZeroValue extends Value {
    public ZeroValue() {
        super("0", new IntType(32));
    }
}
