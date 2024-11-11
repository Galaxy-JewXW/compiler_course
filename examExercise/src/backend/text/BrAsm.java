package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

import java.util.Objects;

public class BrAsm extends TextAssembly {
    private final String label;
    private final Register rs;
    private final AsmOp op;
    private final Register rt;
    private final int number;

    public BrAsm(String label, Register rs, AsmOp op, Register rt) {
        this.label = label;
        this.rs = rs;
        this.op = op;
        this.rt = rt;
        this.number = 0;
    }

    public BrAsm(String label, Register rs, AsmOp op, int number) {
        this.label = label;
        this.rs = rs;
        this.op = op;
        this.rt = null;
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public AsmOp getOp() {
        return op;
    }

    public Register getRs() {
        return rs;
    }

    public Register getRt() {
        return rt;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return op.name().toLowerCase() + " " + rs
                + ", " + Objects.requireNonNullElse(rt, number) + ", " + label;
    }
}
