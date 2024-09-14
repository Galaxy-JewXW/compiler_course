package frontend.syntax;

import frontend.TokenType;

// 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block // 存在main函数
public class MainFuncDef extends SyntaxNode {
    private final Block block;

    public MainFuncDef(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.INTTK));
        System.out.println(TokenType.printType(TokenType.MAINTK));
        System.out.println(TokenType.printType(TokenType.LPARENT));
        System.out.println(TokenType.printType(TokenType.RPARENT));
        block.print();
        System.out.println("<MainFuncDef>");
    }
}
