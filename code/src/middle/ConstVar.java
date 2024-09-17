package middle;

import middle.model.Value;
import middle.types.Assignable;
import middle.types.ValueType;

public class ConstVar extends Value implements Assignable {
    public ConstVar(String name, ValueType valueType) {
        super(name, valueType);
    }
}
