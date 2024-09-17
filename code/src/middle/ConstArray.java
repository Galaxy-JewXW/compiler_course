package middle;

import middle.model.Value;
import middle.types.ArrayType;
import middle.types.ValueType;

import java.util.ArrayList;
import java.util.StringJoiner;

public class ConstArray extends ConstVar {
    private final ArrayList<Value> elements = new ArrayList<>();

    public ConstArray() {
        super("", null);
    }

    public ConstArray(ValueType type) {
        super("", type);
    }

    public ArrayList<Value> getElements() {
        return elements;
    }

    public void addElement(Value element) {
        elements.add(element);
    }

    public void resetType() {
        setValueType(new ArrayType(elements.get(0).getValueType(), elements.size()));
    }

    public boolean allZero() {
        for (Value value : elements) {
            if (value instanceof ConstInt constInt) {
                if (constInt.getIntValue() != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (allZero()) {
            return getValueType().toString() + " zeroinitializer";
        } else {
            StringJoiner sj = new StringJoiner(", ", getValueType().toString() + " [", "]");
            for (Value value : elements) {
                sj.add(value.toString());
            }
            return sj.toString();
        }
    }
}
