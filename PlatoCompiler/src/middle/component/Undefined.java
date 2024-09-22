package middle.component;

import middle.component.model.Value;
import middle.component.type.IntegerType;

public class Undefined extends Value {
    public Undefined() {
        super("undefined", IntegerType.VOID);
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
