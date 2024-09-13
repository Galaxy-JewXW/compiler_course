package node.func;

import IO.OutputHandler;
import error.ErrorHandler;
import node.Block;
import error.symbol.FuncSymbol;
import error.symbol.Type;
import token.TokenType;

import java.util.ArrayList;

public class MainFuncDef {
    //MainFuncDef → 'int' 'main' '(' ')' Block
    private Block block;

    public MainFuncDef(Block block) {
        this.block = block;
    }

    public void print() {
        OutputHandler.printToken(TokenType.INTTK);
        OutputHandler.printToken(TokenType.MAINTK);
        OutputHandler.printToken(TokenType.LPARENT);
        OutputHandler.printToken(TokenType.RPARENT);
        block.print();
        OutputHandler.println("<MainFuncDef>");
    }

    public void checkError() {
        ErrorHandler.getInstance().addSymbol(
                new FuncSymbol("main", Type.INT, new ArrayList<>()));
        ErrorHandler.getInstance().addSymbolTable(Type.INT);
        block.checkError();
        ErrorHandler.getInstance().removeSymbolTable();
    }

    public Block getBlock() {
        return block;
    }
}
