package frontend.syntax;

import frontend.TokenType;

import java.util.ArrayList;

// 语句块 Block → '{' { BlockItem } '}'
public class Block extends SyntaxNode {
    private final ArrayList<BlockItem> blockItems;
    private final int endLine;

    public Block(ArrayList<BlockItem> blockItems, int endLine) {
        this.blockItems = blockItems;
        this.endLine = endLine;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    public int getEndLine() {
        return endLine;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.LBRACE));
        for (BlockItem blockItem : blockItems) {
            blockItem.print();
        }
        System.out.println(TokenType.printType(TokenType.RBRACE));
        System.out.println("<Block>");
    }
}
