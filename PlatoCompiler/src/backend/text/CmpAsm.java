package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class CmpAsm extends TextAssembly {
    private final Register rd;
    private final AsmOp op;
    private final Register rs;
    private final Register rt;

    public CmpAsm(Register rd, AsmOp op, Register rs, Register rt) {
        this.rd = rd;
        this.op = op;
        this.rs = rs;
        this.rt = rt;
    }

    public Register getRs() {
        return rs;
    }

    public Register getRt() {
        return rt;
    }

    public Register getRd() {
        return rd;
    }

    @Override
    public String toString() {
        return op.name().toLowerCase() + " " + rd + ", " + rs + ", " + rt;
    }
}
