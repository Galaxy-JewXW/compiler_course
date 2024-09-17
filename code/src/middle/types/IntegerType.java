package middle.types;

public class IntegerType extends ValueType {
    private final int bits;

    public IntegerType(int bits) {
        this.bits = bits;
    }

    public static final IntegerType i32 = new IntegerType(32);
    public static final IntegerType i8 = new IntegerType(8);
    public static final IntegerType i1 = new IntegerType(1);

    public int getBits() {
        return bits;
    }

    @Override
    public String toString() {
        return "i" + bits;
    }
}
