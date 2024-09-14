package frontend.syntax;

import frontend.TokenType;

import java.util.ArrayList;

// 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
// 1.花括号内重复0次 2.花括号内重复多次
public class VarDecl extends Decl {
    private final BType bType;
    private final ArrayList<VarDef> varDefs;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs) {
        this.bType = bType;
        this.varDefs = varDefs;
    }

    public BType getBType() {
        return bType;
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }

    @Override
    public void print() {
        bType.print();
        for (int i = 0; i < varDefs.size(); i++) {
            if (i > 0) {
                System.out.println(TokenType.printType(TokenType.COMMA));
            }
            varDefs.get(i).print();
        }
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<VarDecl>");
    }
}
