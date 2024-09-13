package backend.Symbol;

import IR.values.Value;

public class MipsSymbol {
    private String base;//$gp,$fp
    private final int off;
    private final Value val;

    public MipsSymbol(String base, int off, Value value) {
        this.off = off;
        this.val = value;
    }

    public int getOff() {
        return off;
    }

    public Value getVal() {
        return val;
    }

    public String getBase() {
        return base;
    }
}
