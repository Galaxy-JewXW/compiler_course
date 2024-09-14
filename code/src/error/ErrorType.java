package error;

public enum ErrorType {
    IllegalSymbol("a"),
    IdentRedefined("b"),
    IdentUndefined("c"),
    ParamSizeMismatch("d"),
    ParamTypeMismatch("e"),
    ReturnTypeError("f"),
    ReturnMissing("g"),
    ConstAssign("h"),
    SEMICNMissing("i"),
    RPARENTMissing("j"),
    RBRACKMissing("k"),
    PrintfFmtCntNotMatch("l"),
    BreakContinueNotInLoop("m");

    private final String name;

    ErrorType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
