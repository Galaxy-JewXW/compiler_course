package llvm.types;

public class VoidType implements ValueType {
    @Override
    public String toString() {
        return "void";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass();
    }

}
