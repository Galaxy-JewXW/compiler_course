package backend.text;

import backend.enums.Register;

public class NegAsm extends TextAssembly {
    private final Register dst;
    private final Register src;

    public NegAsm(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
    }

    public Register getDst() {
        return dst;
    }

    public Register getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return "neg " + dst + ", " + src;
    }
}
