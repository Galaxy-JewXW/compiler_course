package IR.types;

import java.util.ArrayList;

public class ArrayType implements Type {
    private final Type elementType;
    private final int len;

    public ArrayType(Type elementType, int len) {
        this.elementType = elementType;
        this.len = len;
    }

    public int getLen() {
        return len;
    }

    public Type getElementType() {
        return elementType;
    }

    public int getCapacity() {
        if (elementType instanceof IntegerType) {
            return len;
        } else {
            return len * ((ArrayType) elementType).getCapacity();
        }
    }

    public ArrayList<Integer> getDims() {
        ArrayList<Integer> res = new ArrayList<>();
        Type ptr = this;
        while (ptr instanceof ArrayType arrayType) {
            res.add(arrayType.getLen());
            ptr = arrayType.elementType;
        }
        return res;
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", len, elementType);
    }
}
