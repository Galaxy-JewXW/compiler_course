package backend.text;

import backend.enums.Register;

public class LaAsm extends TextAssembly {
    private final Register target;
    private final String pointer;

    public LaAsm(Register target, String pointer) {
        this.target = target;
        this.pointer = pointer;
    }

    public Register getTarget() {
        return target;
    }

    public String getPointer() {
        return pointer;
    }

    @Override
    public String toString() {
        return "la " + target + ", " + pointer;
    }
}
