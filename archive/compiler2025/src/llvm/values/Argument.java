package llvm.values;

import llvm.types.Type;

public class Argument extends Value {
    public Argument(Type type) {
        super("%" + valueIdCount, type);
        valueIdCount++;
    }

    public void refreshName() {
        setName("%" + valueIdCount);
        valueIdCount++;
    }

    @Override
    public String toString() {
        return getType().toString() + ' ' + getName();
    }
}
