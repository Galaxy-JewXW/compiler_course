package middle.component;

import middle.component.types.IntegerType;
import middle.component.types.PointerType;

public class ConstString extends ConstVar {
    private final String string;
    private final int length;

    public ConstString(String string) {
        super("\"" + string.replace("\n", "\\n") + "\"", new PointerType(IntegerType.i8));
        this.string = string.replace("\n", "\\0A") + "\\00";
        this.length = string.length() + 1;
    }

    public String getString() {
        return string;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "[" + length + " x i8] c\"" + string + '\"';
    }
}
