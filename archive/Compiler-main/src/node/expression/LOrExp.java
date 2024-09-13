package node.expression;

import IO.OutputHandler;
import token.TokenType;

import java.util.ArrayList;

public class LOrExp {
    // LOrExp → LAndExp | LOrExp '||' LAndExp
    private ArrayList<LAndExp> lAndExps;

    public LOrExp(ArrayList<LAndExp> lAndExps) {
        this.lAndExps = lAndExps;
    }

    public ArrayList<LAndExp> getlAndExps() {
        return lAndExps;
    }

    public void print() {
        lAndExps.get(0).print();
        for (int i = 1; i < lAndExps.size(); i++) {
            OutputHandler.println("<LOrExp>");
            OutputHandler.printToken(TokenType.OR);
            lAndExps.get(i).print();
        }
        OutputHandler.println("<LOrExp>");
    }

    public void checkError() {
        for (LAndExp lAndExp : lAndExps) {
            lAndExp.checkError();
        }
    }
}
