package node.expression;

import IO.OutputHandler;
import token.Token;

import java.util.ArrayList;

public class RelExp {
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private ArrayList<AddExp> addExps;
    private ArrayList<Token> ops;

    public RelExp(ArrayList<AddExp> addExps, ArrayList<Token> ops) {
        this.addExps = addExps;
        this.ops = ops;
    }

    public ArrayList<AddExp> getAddExps() {
        return addExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void print() {
        addExps.get(0).print();
        for (int i = 1; i < addExps.size(); i++) {
            OutputHandler.println("<RelExp>");
            OutputHandler.printToken(ops.get(i - 1));
            addExps.get(i).print();
        }
        OutputHandler.println("<RelExp>");
    }

    public void checkError() {
        for (AddExp addExp : addExps) {
            addExp.checkError();
        }
    }

}
