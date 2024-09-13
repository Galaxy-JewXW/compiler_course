package node.expression;

import IO.OutputHandler;
import token.TokenType;

import java.util.ArrayList;

public class LAndExp {
    //LAndExp → EqExp | LAndExp '&&' EqExp
    private ArrayList<EqExp> eqExps;

    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }

    public void print() {
        eqExps.get(0).print();
        for (int i = 1; i < eqExps.size(); i++) {
            OutputHandler.println("<LAndExp>");
            OutputHandler.printToken(TokenType.AND);
            eqExps.get(i).print();
        }
        OutputHandler.println("<LAndExp>");
    }

    public void checkError() {
        for (EqExp eqExp : eqExps) {
            eqExp.checkError();
        }
    }
}
