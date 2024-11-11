package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

public class MemAsm extends TextAssembly {
    private final AsmOp op;
    private final Register rd;
    private final Register base;
    private final int offset;

    public MemAsm(AsmOp op, Register rd, Register base, int offset) {
        this.op = op;
        this.rd = rd;
        this.base = base;
        this.offset = offset;
    }

    public AsmOp getOp() {
        return op;
    }

    public Register getRd() {
        return rd;
    }

    public Register getBase() {
        return base;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return op.name().toLowerCase() + " " + rd + ", " + offset + "(" + base + ")";
    }
}
