package middle.component;

import middle.component.model.Value;
import middle.component.types.Assignable;
import middle.component.types.ValueType;

public class ConstVar extends Value implements Assignable {
    public ConstVar(String name, ValueType valueType) {
        super(name, valueType);
    }
}
