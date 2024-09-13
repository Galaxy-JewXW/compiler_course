package syntax;

import syntax.statement.Stmt;

// 语句块项 BlockItem -> Decl | Stmt
public class BlockItem {
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

    public Stmt getStmt() {
        return stmt;
    }

    public Decl getDecl() {
        return decl;
    }

    public void check() {
        if (decl != null) {
            decl.check();
        } else if (stmt != null) {
            stmt.check();
        }
    }

    public void output() {
        if (decl != null) {
            decl.output();
        } else if (stmt != null) {
            stmt.output();
        }
    }
}
