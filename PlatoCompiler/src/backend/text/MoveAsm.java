package backend.text;

import backend.enums.Register;

public class MoveAsm extends TextAssembly {
    private final Register dst;
    private final Register src;

    public MoveAsm(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
    }

    @Override
    public String toString() {
        return "move " + dst + " " + src;
    }
}
