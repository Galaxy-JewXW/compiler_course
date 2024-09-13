package llvm.types;

public class PointerType implements ValueType {
    private final ValueType pointToType;

    public PointerType(ValueType pointToType) {
        this.pointToType = pointToType;
    }

    public ValueType getPointToType() {
        return pointToType;
    }

    @Override
    public String toString() {
        return pointToType.toString() + "*";
    }
}
