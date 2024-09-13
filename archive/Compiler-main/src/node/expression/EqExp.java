package node.expression;

import IO.OutputHandler;
import token.Token;

import java.util.ArrayList;

public class EqExp {
    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    private ArrayList<RelExp> relExps;
    private ArrayList<Token> ops;

    public EqExp(ArrayList<RelExp> relExps, ArrayList<Token> ops) {
        this.relExps = relExps;
        this.ops = ops;
    }

    public ArrayList<RelExp> getRelExps() {
        return relExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void print() {
        relExps.get(0).print();
        for (int i = 1; i < relExps.size(); i++) {
            OutputHandler.println("<EqExp>");
            OutputHandler.printToken(ops.get(i - 1));
            relExps.get(i).print();
        }
        OutputHandler.println("<EqExp>");
    }

    public void checkError() {
        for (RelExp relExp : relExps) {
            relExp.checkError();
        }
    }
}
