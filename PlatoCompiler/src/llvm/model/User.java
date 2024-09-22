package llvm.model;

import llvm.type.ValueType;

import java.util.ArrayList;

public class User extends Value {
    private final ArrayList<Value> operands = new ArrayList<>();

    public User(String name, ValueType type) {
        super(name, type);
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void modifyOperand(Value value, Value newValue) {
        if (!operands.contains(value)) {
            return;
        }
        int index = operands.indexOf(value);
        value.deleteUser(this);
        operands.set(index, newValue);
        newValue.addUse(this);
    }
}
