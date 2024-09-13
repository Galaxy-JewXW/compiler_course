package syntax;

// 声明 Decl -> ConstDecl | VarDecl
public interface Decl {
    void output();
    void check();
}
