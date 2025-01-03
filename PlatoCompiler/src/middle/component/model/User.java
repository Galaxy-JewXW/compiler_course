package middle.component.model;

import middle.component.type.ValueType;

import java.util.ArrayList;

public class User extends Value {
    private ArrayList<Value> operands = new ArrayList<>();

    public User(String name, ValueType type) {
        super(name, type);
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperand(Value value) {
        operands.add(value);
        value.addUse(this);
    }

    public void removeOperands() {
        for (Value value : operands) {
            if (value != null) {
                value.deleteUser(this);
            }
        }
    }

    public void modifyOperand(Value value, Value newValue) {
        if (!operands.contains(value)) {
            return;
        }
        int index = operands.indexOf(value);
        while (index != -1) {
            operands.set(index, newValue);
            newValue.addUse(this);
            index = operands.indexOf(value);
        }
    }
}
