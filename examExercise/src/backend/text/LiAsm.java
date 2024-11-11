package backend.text;

import backend.enums.Register;

public class LiAsm extends TextAssembly {
    private final Register target;
    private final int immediate;

    public LiAsm(Register target, int immediate) {
        this.target = target;
        this.immediate = immediate;
    }

    public Register getTarget() {
        return target;
    }

    public int getImmediate() {
        return immediate;
    }

    @Override
    public String toString() {
        return "li " + target + ", " + immediate;
    }
}
