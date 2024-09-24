package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class BrAsm extends TextAssembly {
    private final String label;
    private final Register rs;
    private final AsmOp op;
    private final Register rt;

    public BrAsm(String label, Register rs, AsmOp op, Register rt) {
        this.label = label;
        this.rs = rs;
        this.op = op;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return op.name().toLowerCase() + " " + rs
                + " " + rt + " " + label;
    }
}
