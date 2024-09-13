package llvm.values;


import llvm.types.IntegerType;


public class ConstInt extends Const {
    private final int intValue;
    public static final ConstInt ZERO = new ConstInt(0);

    public ConstInt(int intValue) {
        super(String.valueOf(intValue), IntegerType.i32);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return "i32 " + intValue;
    }
}
