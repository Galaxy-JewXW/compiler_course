package middle.component.type;

public class ValueType {
    protected ValueType() {
    }

    public int getLength() {
        if (this instanceof ArrayType) {
            int eleNum = ((ArrayType) this).getElementNum();
            ValueType elementType = ((ArrayType) this).getElementType();
            return eleNum * elementType.getLength();
        } else return 1;
    }
}
