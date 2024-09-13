package llvm.types;

public class ArrayType implements ValueType {
    private final ValueType elementType;
    private final int length;

    public ArrayType(ValueType valueType, int length) {
        this.elementType = valueType;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public ValueType getElementType() {
        return elementType;
    }

    public int getCapacity() {
        if (elementType instanceof IntType) {
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
