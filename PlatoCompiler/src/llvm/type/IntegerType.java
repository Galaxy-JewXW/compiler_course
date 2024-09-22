package llvm.type;

public class IntegerType extends ValueType {
    private final int bits;

    public IntegerType(int bits) {
        this.bits = bits;
    }

    @Override
    public String toString() {
        if (bits < 0) {
            return "undefined";
        } else if (bits == 0) {
            return "void";
        } else {
            return "i" + bits;
        }
    }

    public static final IntegerType UNDEFINED = new IntegerType(-1);
    public static final IntegerType VOID = new IntegerType(0);
    public static final IntegerType i1 = new IntegerType(1);
    public static final IntegerType i8 = new IntegerType(8);
    public static final IntegerType i32 = new IntegerType(32);
}
