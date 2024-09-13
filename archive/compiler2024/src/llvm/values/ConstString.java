package llvm.values;

import llvm.types.IntType;
import llvm.types.PointerType;

public class ConstString extends ConstValue {
    private final String value;
    private final int length;

    public ConstString(String value) {
        super("\"" + value.replace("\n", "\\n") + "\"",
                new PointerType(new IntType(8)));
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
        return "[" + length + " x i8] c\"" + value + "\"";
    }
}
