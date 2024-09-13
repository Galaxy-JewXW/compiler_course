package IR.values;

import IR.types.IntegerType;
import IR.types.Type;

public class NullValue extends Value {

    public NullValue() {
        super("0", IntegerType.i32);
    }
}
