package IR.values;

import IR.types.IntegerType;
import IR.types.PointerType;

public class ConstStr extends Const {
    private String value;
    private int length;

    public ConstStr(String value) {
        super("\"" + value.replace("\n", "\\n") + "\"", new PointerType(IntegerType.i8));
        this.value = value.replace("\n", "\\0a") + "\\00";
        this.length = value.length() + 1;
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return String.format("[%d x i8] c\"%s\"", length, value);
    }
}
