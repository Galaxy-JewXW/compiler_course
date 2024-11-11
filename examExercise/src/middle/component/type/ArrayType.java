package middle.component.type;

public class ArrayType extends ValueType {
    private final int elementNum;
    private final ValueType elementType;

    public ArrayType(int elementNum, ValueType elementType) {
        this.elementNum = elementNum;
        this.elementType = elementType;
    }

    public int getElementNum() {
        return elementNum;
    }

    public ValueType getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return "[" + elementNum + " x " + elementType + "]";
    }

}
