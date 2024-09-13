package llvm.types;

public class IntegerType implements Type {
    private final int bits;

    public IntegerType(int bits) {
        this.bits = bits;
    }

    public boolean isI1() {
        return this.bits == 1;
    }

    public boolean isI8() {
        return this.bits == 8;
    }

    public boolean isI32() {
        return this.bits == 32;
    }

    public static final IntegerType i32 = new IntegerType(32);
    public static final IntegerType i8 = new IntegerType(8);
    public static final IntegerType i1 = new IntegerType(1);

    @Override
    public String toString() {
        return "i" + bits;
    }
}
