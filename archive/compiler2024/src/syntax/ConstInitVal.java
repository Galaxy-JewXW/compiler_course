package syntax;

import frontend.TokenType;
import syntax.expression.ConstExp;

import java.util.ArrayList;

// 常量初值 ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
public class ConstInitVal {
    private final ConstExp constExp;
    private final ArrayList<ConstInitVal> constInitVals;

    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
        this.constInitVals = null;
    }

    public ConstInitVal(ArrayList<ConstInitVal> constInitVals) {
        this.constExp = null;
        this.constInitVals = constInitVals;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ArrayList<ConstInitVal> getConstInitVals() {
        return constInitVals;
    }

    public void output() {
        if (constExp != null) {
            constExp.output();
        } else {
            System.out.println(TokenType.LBRACE.name() + " " + TokenType.LBRACE);
            if (constInitVals != null && constInitVals.get(0) != null) {
                constInitVals.get(0).output();
            }
            if (constInitVals != null) {
                for (int i = 1; i < constInitVals.size(); i++) {
                    System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
                    constInitVals.get(i).output();
                }
            }
            System.out.println(TokenType.RBRACE.name() + " " + TokenType.RBRACE);
        }
        System.out.println("<ConstInitVal>");
    }

    public void check() {
        if (constExp != null) {
            constExp.check();
        } else {
            if (constInitVals != null) {
                for (ConstInitVal constInitVal : constInitVals) {
                    constInitVal.check();
                }
            }
        }
    }
}
