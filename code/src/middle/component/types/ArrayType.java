package middle.component.types;

public class ArrayType extends ValueType {
    private final ValueType elementType;
    private final int length;

    public ArrayType(ValueType elementType, int length) {
        this.elementType = elementType;
        this.length = length;
    }

    public ValueType getElementType() {
        return elementType;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + elementType + "]";
    }
}
