package llvm;

import llvm.model.Value;
import llvm.type.IntegerType;

public class Undefined extends Value {
    public Undefined() {
        super("undefined", IntegerType.VOID);
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
