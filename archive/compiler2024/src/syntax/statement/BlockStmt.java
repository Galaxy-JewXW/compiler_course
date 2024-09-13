package syntax.statement;

import error.ErrorVisitor;
import syntax.Block;

public class BlockStmt implements Stmt {
    private final Block block;

    public BlockStmt(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void output() {
        block.output();
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        errorVisitor.addTable(null);
        block.check();
        errorVisitor.removeTable();
    }
}
