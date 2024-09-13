package IR.types;

public class LabelType implements Type {
    private static int cnt = 0;
    private final int val;

    public LabelType() {
        val = cnt++;
    }

    @Override
    public String toString() {
        return "label_" + val;
    }
}
