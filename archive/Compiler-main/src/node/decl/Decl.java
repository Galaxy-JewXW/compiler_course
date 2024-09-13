package node.decl;

public interface Decl {
    //Decl → ConstDecl | VarDecl
    void print();

    void checkError();
}
