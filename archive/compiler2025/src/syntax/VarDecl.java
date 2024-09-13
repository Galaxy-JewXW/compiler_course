package syntax;

import frontend.TokenType;

import java.util.ArrayList;

public class VarDecl implements Decl {
    private final BType bType;
    private final ArrayList<VarDef> varDefs;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs) {
        this.bType = bType;
        this.varDefs = varDefs;
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }

    @Override
    public void output() {
        bType.output();
        varDefs.get(0).output();
        for (int i = 1; i < varDefs.size(); i++) {
            System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
            varDefs.get(i).output();
        }
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<VarDecl>");
    }

    @Override
    public void check() {
        for (VarDef varDef : varDefs) {
            varDef.check();
        }
    }
}
