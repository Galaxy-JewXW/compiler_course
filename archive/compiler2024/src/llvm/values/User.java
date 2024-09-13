package llvm.values;

import llvm.types.ValueType;

import java.util.ArrayList;

public class User extends Value {
    private final ArrayList<Value> operands = new ArrayList<>();

    public User(String name, ValueType type) {
        super(name, type);
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperand(Value value) {
        operands.add(value);
        value.addUse(new Use(value, this, operands.size() - 1));
    }

    public void replaceVal(int index, Value value) {
        operands.set(index, value);
    }

    public void deleteUse() {
        for (Value value : operands) {
            value.deleteUser(this);
        }
        operands.clear();
    }
}
