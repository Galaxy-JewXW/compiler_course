package IR.values;

import IR.types.ArrayType;
import IR.types.Type;

import java.util.ArrayList;

public class ConstArray extends Const {
    private final ArrayList<Value> values = new ArrayList<>();

    public ConstArray() {
        super("", null);
    }

    public ConstArray(Type type) {
        super("", type);
        Type elementType = ((ArrayType) getType()).getElementType();
        int len = ((ArrayType) getType()).getLen();
        if (elementType instanceof ConstInt) {
            for (int i = 0; i < len; i++) {
                values.add(ConstInt.ZERO);
            }
        } else if (elementType instanceof ConstArray) {
            for (int i = 0; i < len; i++) {
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
                if (constInt.getVal() != 0) {
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
            return String.format("%s zeroinitializer", getType());
        } else {
            StringBuilder res = new StringBuilder(getType().toString() + ' ' + '[');
            res.append(values.get(0).toString());
            for (int i = 1; i < values.size(); i++) {
                res.append(", ").append(values.get(i).toString());
            }
            res.append(']');
            return res.toString();
        }
    }
}
