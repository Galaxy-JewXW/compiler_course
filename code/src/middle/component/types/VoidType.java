package middle.component.types;

public class VoidType extends ValueType {
    public static final VoidType VOID = new VoidType();

    private VoidType() {

    }

    @Override
    public String toString() {
        return "void";
    }
}
