package llvm.types;

public class LabelType implements ValueType {
    private static int cnt = 0;
    private final int value;

    public LabelType() {
        this.value = cnt;
        cnt++;
    }

    @Override
    public String toString() {
        return "Label_" + value;
    }
}
