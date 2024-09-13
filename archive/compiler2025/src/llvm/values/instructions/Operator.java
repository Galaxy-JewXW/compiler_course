package llvm.values.instructions;

public enum Operator {
    ADD("add"), // +
    SUB("sub"), // -
    MUL("mul"), // *
    SDIV("sdiv"), // /
    SREM("srem"), // %
    ICMP_EQ("icmp eq"), // ==
    ICMP_NE("icmp ne"), // !=
    ICMP_SGT("icmp sgt"), // >
    ICMP_SGE("icmp sge"), // >=
    ICMP_SLT("icmp slt"), // <
    ICMP_SLE("icmp sle"), // <=
    ZEXT("zext"),
    CALL("call"),
    ALLOC("alloca"),
    STORE("store"),
    LOAD("load"),
    GEP("getelementptr"),
    PHI("phi"),
    BR("br"),
    RET("ret"),
    MOVE("move");

    private final String name;

    Operator(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
