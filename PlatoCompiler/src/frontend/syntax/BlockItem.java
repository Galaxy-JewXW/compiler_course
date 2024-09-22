package frontend.syntax;

import frontend.syntax.statement.Stmt;

public class BlockItem extends SyntaxNode {
    private final Decl decl;
    private final Stmt stmt;

    public BlockItem(Decl decl) {
        this.decl = decl;
        this.stmt = null;
    }

    public BlockItem(Stmt stmt) {
        this.decl = null;
        this.stmt = stmt;
    }

    public Decl getDecl() {
        return decl;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public void print() {
        if (stmt != null) {
            stmt.print();
        } else if (decl != null) {
            decl.print();
        }
    }
}
