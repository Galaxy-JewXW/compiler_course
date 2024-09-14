package frontend.syntax.statement;

import frontend.syntax.Block;

public class BlockStmt extends Stmt {
    private final Block block;

    public BlockStmt(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void print() {
        block.print();
        System.out.println("<Stmt>");
    }
}
