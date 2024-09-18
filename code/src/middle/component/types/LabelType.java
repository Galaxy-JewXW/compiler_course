package middle.component.types;

public class LabelType extends ValueType {
    private static int labelCount = 0;
    private final int labelId;

    public LabelType() {
        labelId = labelCount++;
    }

    @Override
    public String toString() {
        return "label_" + labelId;
    }
}
