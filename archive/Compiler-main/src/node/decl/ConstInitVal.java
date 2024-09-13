package node.decl;

import IO.OutputHandler;
import node.expression.ConstExp;
import token.TokenType;

import java.util.ArrayList;

public class ConstInitVal {
    //ConstInitVal → ConstExp
    // | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    private ConstExp constExp;
    private ArrayList<ConstInitVal> constInitVals;

    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
    }

    public ConstInitVal(ArrayList<ConstInitVal> constInitVals) {
        this.constInitVals = constInitVals;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ArrayList<ConstInitVal> getConstInitVals() {
        return constInitVals;
    }

    public void print() {
        if (constExp != null) {
            constExp.print();
        } else {
            OutputHandler.printToken(TokenType.LBRACE);
            if (constInitVals.get(0) != null) {
                constInitVals.get(0).print();
            }
            for (int i = 1; i < constInitVals.size(); i++) {
                OutputHandler.printToken(TokenType.COMMA);
                constInitVals.get(i).print();
            }
            OutputHandler.printToken(TokenType.RBRACE);
        }
        OutputHandler.println("<ConstInitVal>");
    }

    public void checkError() {
        if (constExp != null) {
            constExp.checkError();
        } else {
            for (ConstInitVal constInitVal : constInitVals) {
                constInitVal.checkError();
            }
        }
    }
}
