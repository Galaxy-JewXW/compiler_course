package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class JumpAsm extends TextAssembly {
    private final AsmOp op;
    private final String target;
    private final Register rd;

    // j jal
    public JumpAsm(AsmOp op, String target) {
        this.op = op;
        this.target = target;
        this.rd = null;
    }

    // jr
    public JumpAsm(AsmOp op, Register rd) {
        this.op = op;
        this.rd = rd;
        this.target = null;
    }

    @Override
    public String toString() {
        if (op == AsmOp.JR) {
            return op.name().toLowerCase() + " " + rd;
        } else {
            return op.name().toLowerCase() + " " + target;
        }
    }
}
