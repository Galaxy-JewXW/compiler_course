package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import node.Block;

public class BlockStmt implements Stmt {
    //Block
    private Block block;

    public BlockStmt(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void print() {
        block.print();
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        ErrorHandler.getInstance().addSymbolTable(null);//null代表不是函数
        block.checkError();
        ErrorHandler.getInstance().removeSymbolTable();
    }
}
