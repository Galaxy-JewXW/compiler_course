package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class CalcAsm extends TextAssembly {
    private final Register rd;
    private final AsmOp op;
    // R-Type instructions
    private final Register rs;
    private final Register rt;
    // I-Type instructions
    private final int immediate;
    private final boolean isTypeR;

    public CalcAsm(Register rd, AsmOp op, Register rs, Register rt) {
        this.rd = rd;
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.immediate = 0;
        isTypeR = true;
    }

    public CalcAsm(Register rd, AsmOp op, Register rs, int immediate) {
        this.rd = rd;
        this.op = op;
        this.rs = rs;
        this.rt = null;
        this.immediate = immediate;
        isTypeR = false;
    }

    public AsmOp getOp() {
        return op;
    }

    public Register getRd() {
        return rd;
    }

    public Register getRt() {
        return rt;
    }

    public int getImmediate() {
        return immediate;
    }

    public Register getRs() {
        return rs;
    }

    @Override
    public String toString() {
        if (op.ordinal() <= AsmOp.SRLV.ordinal()) {
            return op.name().toLowerCase() + " " + rd
                    + ", " + rs + ", " + rt;
        }
        return op.name().toLowerCase() + " "
                + rd + ", " + rs + ", " + immediate;
    }
}
