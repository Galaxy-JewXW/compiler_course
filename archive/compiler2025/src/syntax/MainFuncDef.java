package syntax;


import error.ErrorVisitor;
import error.symbol.FuncSymbol;
import error.symbol.Type;
import frontend.TokenType;

import java.util.ArrayList;

// 主函数定义 MainFuncDef -> 'int' 'main' '(' ')' Block
public class MainFuncDef {
    private final Block block;

    public MainFuncDef(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void output() {
        System.out.println(TokenType.INTTK.name() + " " + TokenType.INTTK);
        System.out.println(TokenType.MAINTK.name() + " " + TokenType.MAINTK);
        System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
        System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        block.output();
        System.out.println("<MainFuncDef>");
    }

    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        errorVisitor.addSymbol(new FuncSymbol("main", Type.INT, new ArrayList<>()));
        errorVisitor.addTable(Type.INT);
        block.check();
        errorVisitor.removeTable();
    }
}
