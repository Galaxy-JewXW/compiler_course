package node.expression;

import IO.OutputHandler;

public class Exp {
    // Exp → AddExp
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void print() {
        addExp.print();
        OutputHandler.println("<Exp>");
    }

    public void checkError() {
        addExp.checkError();
    }
}
