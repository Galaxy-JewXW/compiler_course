package llvm.values;

import llvm.types.ArrayType;
import llvm.types.Type;

import java.util.ArrayList;
import java.util.StringJoiner;

public class ConstArray extends Const {
    private final ArrayList<Value> values = new ArrayList<>();

    public ConstArray() {
        super("", null);
    }

    public ConstArray(Type type) {
        super("", type);
        Type elementType = ((ArrayType) getType()).getElementType();
        int length = ((ArrayType) getType()).getLength();
        if (elementType instanceof ConstInt) {
            for (int i = 0; i < length; i++) {
                values.add(ConstInt.ZERO);
            }
        } else if (elementType instanceof ConstArray) {
            for (int i = 0; i < length; i++) {
                values.add(new ConstArray(elementType));
            }
        }
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void addVal(Value value) {
        this.values.add(value);
    }

    public void resetType() {
        setType(new ArrayType(values.get(0).getType(), values.size()));
    }

    public boolean allZero() {
        for (Value value : values) {
            if (value instanceof ConstInt constInt) {
                if (constInt.getIntValue() != 0) {
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
