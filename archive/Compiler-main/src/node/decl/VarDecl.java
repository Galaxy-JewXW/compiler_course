package node.decl;

import IO.OutputHandler;
import node.BType;
import token.TokenType;

import java.util.ArrayList;

public class VarDecl implements Decl {
    //VarDecl → BType VarDef { ',' VarDef } ';'
    private BType bType;
    private ArrayList<VarDef> varDefs;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs) {
        this.bType = bType;
        this.varDefs = varDefs;
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }

    public void print() {
        bType.print();
        varDefs.get(0).print();
        for (int i = 1; i < varDefs.size(); i++) {
            OutputHandler.printToken(TokenType.COMMA);
            varDefs.get(i).print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<VarDecl>");
    }

    @Override
    public void checkError() {
        for (VarDef varDef : varDefs) {
            varDef.checkError();
        }
    }
}
