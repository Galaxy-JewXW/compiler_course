package middle.component;

import middle.component.type.IntegerType;

public class Undefined extends ConstInt {
    public Undefined() {
        super(IntegerType.i32, 0);
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
