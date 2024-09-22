package frontend.syntax.variable;

import frontend.syntax.BType;
import frontend.syntax.Decl;
import frontend.token.TokenType;

import java.util.ArrayList;

// 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
// 1.花括号内重复0次 2.花括号内重复多次
public class ConstDecl extends Decl {
    private final BType bType;
    private final ArrayList<ConstDef> constDefs;

    public ConstDecl(BType bType, ArrayList<ConstDef> constDefs) {
        this.bType = bType;
        this.constDefs = constDefs;
    }

    public BType getBType() {
        return bType;
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }

    @Override
    public void print() {
        System.out.println(TokenType.printType(TokenType.CONSTTK));
        bType.print();
        for (int i = 0; i < constDefs.size(); i++) {
            if (i > 0) {
                System.out.println(TokenType.printType(TokenType.COMMA));
            }
            constDefs.get(i).print();
        }
        System.out.println(TokenType.printType(TokenType.SEMICN));
        System.out.println("<ConstDecl>");
    }
}
