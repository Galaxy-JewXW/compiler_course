package middle.component.type;

public class LabelType extends ValueType {
    private static int cnt = 1;
    private final int id;

    public LabelType() {
        id = cnt++;
    }

    @Override
    public String toString() {
        return "LabelType [id=" + id + "]";
    }
}
