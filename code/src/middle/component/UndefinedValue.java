package middle.component;

import middle.component.model.Value;
import middle.component.types.IntegerType;

public class UndefinedValue extends Value {
    public UndefinedValue() {
        super("0", IntegerType.i32);
    }
}
