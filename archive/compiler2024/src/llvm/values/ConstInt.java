package llvm.values;

import llvm.types.IntType;

public class ConstInt extends ConstValue {
    private final int value;

    public ConstInt(int value) {
        super(Integer.toString(value), new IntType(32));
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getType().toString() + " " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null){
            return false;
        }
        if (obj.getClass() != this.getClass()){
            return false;
        }
        ConstInt constInt = (ConstInt) obj;
        return this.value == constInt.value;
    }
}
