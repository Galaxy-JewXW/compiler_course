package llvm.types;

import java.util.ArrayList;

public class ArrayType implements Type {
    private final Type elementType;
    private final int length;

    public ArrayType(Type elementType, int length) {
        this.elementType = elementType;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public Type getElementType() {
        return elementType;
    }

    public int getCapacity() {
        if (elementType instanceof IntegerType) {
            return length;
        } else {
            return length * ((ArrayType) elementType).getCapacity();
        }
    }

    @Override
    public String toString() {
        return "[" + length + " x " + elementType + "]";
    }
}
