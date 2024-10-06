package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class MDRegAsm extends TextAssembly {
    // mflo mfhi mtlo mthi
    private final AsmOp op;
    private final Register rd;

    public MDRegAsm(AsmOp op, Register rd) {
        this.op = op;
        this.rd = rd;
    }

    public AsmOp getOp() {
        return op;
    }

    public Register getRd() {
        return rd;
    }

    @Override
    public String toString() {
        return op.name().toLowerCase() + " " + rd;
    }
}
