package syntax;

import frontend.TokenType;

import java.util.ArrayList;

// 常量声明 ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
public class ConstDecl implements Decl {
    private final BType bType;
    private final ArrayList<ConstDef> constDefs;

    public ConstDecl(BType bType, ArrayList<ConstDef> constDefs) {
        this.bType = bType;
        this.constDefs = constDefs;
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }

    @Override
    public void output() {
        System.out.println(TokenType.CONSTTK.name() + " " + TokenType.CONSTTK);
        bType.output();
        constDefs.get(0).output();
        for (int i = 1; i < constDefs.size(); i++) {
            System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
            constDefs.get(i).output();
        }
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<ConstDecl>");
    }

    @Override
    public void check() {
        for (ConstDef constDef : constDefs) {
            constDef.check();
        }
    }
}
