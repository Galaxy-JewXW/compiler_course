package llvm.values;

import llvm.types.Type;

import java.util.ArrayList;

public class User extends Value {
    private final ArrayList<Value> operands;

    public User(String name, Type type) {
        super(name, type);
        this.operands = new ArrayList<>();
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperand(Value value) {
        this.operands.add(value);
        value.addUse(new Use(value, this, operands.size() - 1));
    }

    public void replaceVal(int index, Value newVal) {
        operands.set(index, newVal);
    }

    public void deleteUse() {
        for (Value value : operands) {
            value.deleteUser(this);
        }
        this.operands.clear();
    }
}
