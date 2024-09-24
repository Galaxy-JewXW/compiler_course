package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class MDAsm extends TextAssembly {
    private final Register rs;
    private final AsmOp op;
    private final Register rt;

    public MDAsm(Register rs, AsmOp op, Register rt) {
        this.rs = rs;
        this.op = op;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return op.name().toLowerCase() + " " + rs + " " + rt;
    }
}
