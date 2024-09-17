package middle.types;

public class PointerType extends ValueType {
    private final ValueType targetType;

    public PointerType(ValueType targetType) {
        this.targetType = targetType;
    }

    public ValueType getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return targetType.toString() + "*";
    }
}
