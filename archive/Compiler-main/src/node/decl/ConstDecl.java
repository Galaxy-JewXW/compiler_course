package node.decl;

import IO.OutputHandler;
import node.BType;
import token.TokenType;

import java.util.ArrayList;

public class ConstDecl implements Decl {
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private BType bType;
    private ArrayList<ConstDef> constDefs;

    public ConstDecl(BType BType, ArrayList<ConstDef> constDefs) {
        this.bType = BType;
        this.constDefs = constDefs;
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }

    public void print() {
        OutputHandler.printToken(TokenType.CONSTTK);
        bType.print();
        constDefs.get(0).print();
        for (int i = 1; i < constDefs.size(); i++) {
            OutputHandler.printToken(TokenType.COMMA);
            constDefs.get(i).print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<ConstDecl>");
    }

    public void checkError() {
        for (ConstDef constDef : constDefs) {
            constDef.checkError();
        }
    }
}
