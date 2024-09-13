package node;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.BlockItem;
import node.stmt.BlockStmt;
import node.stmt.ReturnStmt;
import node.stmt.Stmt;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class Block {
    // Block → '{' { BlockItem } '}'
    private ArrayList<BlockItem> blockItems;
    private int endLine;//the line of '}'

    public Block(ArrayList<BlockItem> blockItems, int endLine) {
        this.blockItems = blockItems;
        this.endLine = endLine;
    }

    public void print() {
        OutputHandler.printToken(TokenType.LBRACE);
        for (BlockItem blockItem : blockItems) {
            blockItem.print();
        }
        OutputHandler.printToken(TokenType.RBRACE);
        OutputHandler.println("<Block>");
    }

    public void checkError() {
        //addSymbolTable已经在funcDef中执行过了
        for (BlockItem blockItem : blockItems) {
            blockItem.checkError();
        }
        if (ErrorHandler.getInstance().inIntFunc()) {
            if (blockItems.isEmpty() || blockItems.get(blockItems.size() - 1).getStmt() == null
                    || (!(blockItems.get(blockItems.size() - 1).getStmt() instanceof ReturnStmt))) {
                ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.g, endLine));
            }
        }
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }
}
