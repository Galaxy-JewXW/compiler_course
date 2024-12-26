package middle.component.instruction;

public enum OperatorType {
    ADD("add"), // +
    SUB("sub"), // -
    MUL("mul"), // *
    SDIV("sdiv"), // /
    SREM("srem"), // %
    SFUCK("sfuck"),
    ICMP_EQ("icmp eq"), // ==
    ICMP_NE("icmp ne"), // !=
    ICMP_SGT("icmp sgt"), // >
    ICMP_SGE("icmp sge"), // >=
    ICMP_SLT("icmp slt"), // <
    ICMP_SLE("icmp sle"), // <=
    ZEXT("zext"),
    TRUNC("trunc"),
    CALL("call"),
    // 这里把io指令作为call的一种特例提出来看待
    IO("io"),
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

    public static boolean isLogicalOperator(OperatorType opType) {
        return opType == OperatorType.ICMP_EQ || opType == OperatorType.ICMP_NE ||
                opType == OperatorType.ICMP_SLE || opType == OperatorType.ICMP_SLT ||
                opType == OperatorType.ICMP_SGE || opType == OperatorType.ICMP_SGT;
    }
}
