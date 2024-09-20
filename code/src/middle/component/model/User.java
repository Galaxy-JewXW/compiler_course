package middle.component.model;

import middle.component.types.ValueType;

import java.util.ArrayList;

public class User extends Value {
    private final ArrayList<Value> operands = new ArrayList<>();

    public User(String name, ValueType valueType) {
        super(name, valueType);
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperand(Value operand) {
        operands.add(operand);
        operand.addUse(new Use(operand, this, operands.size() - 1));
    }

    public void deleteUse() {
        for (Value value : operands) {
            value.deleteUser(this);
        }
        this.operands.clear();
    }

    public void replaceValue(Value newValue, int index) {
        operands.set(index, newValue);
    }
}
