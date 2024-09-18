package middle.component.instructions;

public enum OperatorType {
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
    TRUNC("trunc"),
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

    OperatorType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
