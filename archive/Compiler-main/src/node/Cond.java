package node;

import IO.OutputHandler;
import node.expression.LOrExp;

public class Cond {
    // Cond → LOrExp
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }

    public void print() {
        lOrExp.print();
        OutputHandler.println("<Cond>");
    }

    public void checkError() {
        lOrExp.checkError();
    }
}
