package node.expression;

import IO.OutputHandler;
import token.Token;

import java.util.ArrayList;

public class AddExp {
    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    private ArrayList<MulExp> mulExps;
    private ArrayList<Token> ops;

    public AddExp(ArrayList<MulExp> mulExps, ArrayList<Token> ops) {
        this.mulExps = mulExps;
        this.ops = ops;
    }

    public ArrayList<MulExp> getMulExps() {
        return mulExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void print() {
        mulExps.get(0).print();
        for (int i = 1; i < mulExps.size(); i++) {
            OutputHandler.println("<AddExp>");
            OutputHandler.printToken(ops.get(i - 1));
            mulExps.get(i).print();
        }
        OutputHandler.println("<AddExp>");
    }

    public void checkError() {
        for (MulExp mulExp : mulExps) {
            mulExp.checkError();
        }
    }
}
