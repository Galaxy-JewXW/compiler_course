package IR.values;

import IR.types.PointerType;
import IR.types.Type;

public class Argument extends Value {
    public Argument(Type type) {
        super("%" + valNumber++, type);
    }

    public void refreshName() {
        setName("%" + valNumber++);
    }

    @Override
    public String toString() {
        return getType().toString() + ' ' + getName();
    }
}
