package llvm.values;

import llvm.types.ArrayType;
import llvm.types.ValueType;

import java.util.ArrayList;
import java.util.StringJoiner;

public class ConstArray extends ConstValue {
    private final ArrayList<Value> values = new ArrayList<>();

    public ConstArray(ValueType type) {
        super("", type);
        ValueType elementType = ((ArrayType) getType()).getElementType();
        int length = ((ArrayType) getType()).getLength();
        if (elementType instanceof ConstInt) {
            for (int i = 0; i < length; i++) {
                values.add(new ConstInt(0));
            }
        } else if (elementType instanceof ConstArray) {
            for (int i = 0; i < length; i++) {
                values.add(new ConstArray(elementType));
            }
        }
    }

    public ConstArray() {
        super("", null);
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void addValue(Value value) {
        values.add(value);
    }

    public void resetType() {
        setType(new ArrayType(values.get(0).getType(), values.size()));
    }

    public boolean allZero() {
        for (Value value : values) {
            if (value instanceof ConstInt constInt) {
                if (constInt.getValue() != 0) {
                    return false;
                }
            } else if (value instanceof ConstArray constArray) {
                if (!constArray.allZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (allZero()) {
            return getType().toString() + " zeroinitializer";
        } else {
            StringJoiner sj = new StringJoiner(", ", getType().toString() + " [", "]");
            for (Value value : values) {
                sj.add(value.toString());
            }
            return sj.toString();
        }
    }
}
