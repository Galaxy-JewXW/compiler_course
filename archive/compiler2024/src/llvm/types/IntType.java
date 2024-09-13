package llvm.types;

public class IntType implements ValueType {
    private final int bits;

    public IntType(int bits) {
        this.bits = bits;
    }

    public int getBits() {
        return bits;
    }

    @Override
    public String toString() {
        return "i" + bits;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null){
            return false;
        }
        if (obj.getClass() != this.getClass()){
            return false;
        }
        IntType type = (IntType) obj;
        return this.bits == type.bits;
    }
}
