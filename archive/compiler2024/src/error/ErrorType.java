package error;

public enum ErrorType {
    IllegalChar("a"), // a
    IdentRedefined("b"), // b
    IdentUndefined("c"), // c
    ParaNumNotMatch("d"), // d
    ParaTypeNotMatch("e"), // e
    ReturnTypeError("f"), // f
    ReturnMissing("g"), // g
    ConstAssign("h"), // h
    SEMICNMissing("i"), // i
    RPARENTMissing("j"), // j
    RBRACKMissing("k"), // k
    PrintfFormatStrNumNotMatch("l"), // l
    BreakContinueNotInLoop("m"); // m

    private final String name;

    ErrorType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}