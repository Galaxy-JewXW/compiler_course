package llvm.types;

public class LabelType implements Type {
    private static int labelCnt = 0;
    private final int labelVal;

    public LabelType() {
        labelVal = labelCnt;
        labelCnt++;
    }

    @Override
    public String toString() {
        return "Label_" + labelVal;
    }
}
