package llvm.values;

import llvm.types.ValueType;

import java.util.ArrayList;

public class Value {
    private String name;
    private ValueType type;
    private final ArrayList<Use> uses = new ArrayList<>();
    protected static int valueCnt = 0;

    public Value(String name, ValueType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ValueType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public ArrayList<Use> getUses() {
        return uses;
    }

    public void replaceVal(Value value) {
        for (Use use : uses) {
            use.getUser().replaceVal(use.getPos(), value);
            use.setValue(value);
            value.addUse(use);
        }
    }

    public void addUse(Use use) {
        uses.add(use);
    }

    public void deleteUser(User user) {
        uses.removeIf(use -> use.getUser() == user);
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }
}
