package frontend;

public enum TokenType {
    /* 保留字 */
    MAINTK("main"), // main
    CONSTTK("const"), // const
    INTTK("int"), // int
    BREAKTK("break"), // break
    CONTINUETK("continue"), // continue
    IFTK("if"), // if
    ELSETK("else"), // else

    FORTK("for"), // for
    GETINTTK("getint"), // getint
    PRINTFTK("printf"), // printf
    RETURNTK("return"), // return
    VOIDTK("void"), // void

    /* 其他 */
    IDENFR("Ident"), // Ident
    INTCON("IntConst"), // IntConst
    STRCON("FormatString"), // FormatString
    NOT("!"), // !
    AND("&&"), // &&
    OR("||"), // ||
    PLUS("+"), // +
    MINU("-"), // -
    MULT("*"), // *
    DIV("/"), // /
    MOD("%"), // %
    LSS("<"), // <
    LEQ("<="), // <=
    GRE(">"), // >
    GEQ(">="), // >=
    EQL("=="), // ==
    NEQ("!="), // !=
    ASSIGN("="), // =
    SEMICN(";"), // ;
    COMMA(","), // ,
    LPARENT("("), // (
    RPARENT(")"), // )
    LBRACK("["), // [
    RBRACK("]"), // ]
    LBRACE("{"), // {
    RBRACE("}");  // }

    private final String name;

    TokenType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}