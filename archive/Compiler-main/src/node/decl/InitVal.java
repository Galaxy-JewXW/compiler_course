package node.decl;

import IO.OutputHandler;
import node.expression.Exp;
import token.TokenType;

import java.util.ArrayList;

public class InitVal {
    //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}
    private Exp exp;
    private ArrayList<InitVal> initVals;

    public InitVal(Exp exp) {
        this.exp = exp;
    }

    public InitVal(ArrayList<InitVal> initVals) {
        this.initVals = initVals;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }

    public void print() {
        if (exp != null) {
            exp.print();
        } else {
            OutputHandler.printToken(TokenType.LBRACE);
            if (initVals.get(0) != null) {
                initVals.get(0).print();
            }
            for (int i = 1; i < initVals.size(); i++) {
                OutputHandler.printToken(TokenType.COMMA);
                initVals.get(i).print();
            }
            OutputHandler.printToken(TokenType.RBRACE);
        }
        OutputHandler.println("<InitVal>");
    }

    public void checkError() {
        if (exp != null) {
            exp.checkError();
        } else {
            for (InitVal initVal : initVals) {
                initVal.checkError();
            }
        }
    }
}
