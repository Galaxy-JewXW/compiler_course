package llvm.values;


import llvm.types.Type;

public class Const extends Value implements Assignable {
    public Const(String name, Type type) {
        super(name, type);
    }
}
