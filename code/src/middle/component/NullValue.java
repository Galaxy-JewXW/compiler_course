package middle.component;

import middle.component.model.Value;
import middle.component.types.IntegerType;

public class NullValue extends Value {
    public NullValue() {
        super("0", IntegerType.i32);
    }
}
