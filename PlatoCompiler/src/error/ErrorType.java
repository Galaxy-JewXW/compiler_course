package error;

/**
 * 所有可能出现的错误集合
 * 错误类别 a 为词法分析中会出现的错误。
 * 错误类别 i, j, k 为语法分析中会出现的错误。
 * 剩余错误类别均为语义分析中会出现的错误。
 */
public enum ErrorType {
    IllegalSymbol("a"),
    IdentRedefined("b"),
    IdentUndefined("c"),
    ParamSizeMismatch("d"),
    ParamTypeMismatch("e"),
    ReturnTypeMismatch("f"), // 无返回值的函数存在不匹配的return语句
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
