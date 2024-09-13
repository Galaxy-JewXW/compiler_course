package syntax;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.TokenType;
import syntax.statement.ReturnStmt;

import java.util.ArrayList;

// 语句块 Block -> '{' { BlockItem } '}'
public class Block {
    private final ArrayList<BlockItem> blockItems;
    private final int endLine;

    public Block(ArrayList<BlockItem> blockItems, int endLine) {
        this.blockItems = blockItems;
        this.endLine = endLine;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    public void output() {
        System.out.println(TokenType.LBRACE.name() + " " + TokenType.LBRACE);
        for (BlockItem blockItem : blockItems) {
            blockItem.output();
        }
        System.out.println(TokenType.RBRACE.name() + " " + TokenType.RBRACE);
        System.out.println("<Block>");
    }

    public void check() {
        for (BlockItem blockItem : blockItems) {
            blockItem.check();
        }
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (errorVisitor.inIntFunc()) {
            if (blockItems.isEmpty() || blockItems.get(blockItems.size() - 1).getStmt() == null
                    || !(blockItems.get(blockItems.size() - 1).getStmt() instanceof ReturnStmt)) {
                errorVisitor.addError(new ErrorLog(ErrorType.ReturnMissing, endLine));
            }
        }
    }
}
