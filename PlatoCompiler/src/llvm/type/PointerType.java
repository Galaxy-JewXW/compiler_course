package llvm.type;

// 指针类型
public class PointerType extends ValueType {
    // 指针指向的类型
    // 例如，'int*'的targetType就是int
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
