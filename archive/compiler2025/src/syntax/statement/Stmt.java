package syntax.statement;

// 语句 Stmt -> LVal '=' Exp ';'
// | [Exp] ';'
// | Block
// | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
// | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
// | 'break' ';' | 'continue' ';'
// | 'return' [Exp] ';'
// | LVal '=' 'getint''('')'';'
// | 'printf''('FormatString{','Exp}')'';'
public interface Stmt {
    void output();
    void check();
}
