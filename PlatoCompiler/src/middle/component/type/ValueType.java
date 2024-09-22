package middle.component.type;

public class ValueType {
    public int getLength() {
        if (this instanceof ArrayType) {
            int eleNum = ((ArrayType) this).getElementNum();
            ValueType elementType = ((ArrayType) this).getElementType();
            return eleNum * elementType.getLength();
        }
        else return 1;
    }
}
