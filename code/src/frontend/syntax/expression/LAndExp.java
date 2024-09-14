package frontend.syntax.expression;

import frontend.TokenType;
import frontend.syntax.SyntaxNode;

import java.util.ArrayList;

public class LAndExp extends SyntaxNode {
    private final ArrayList<EqExp> eqExps;

    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }

    @Override
    public void print() {
        eqExps.get(0).print();
        for (int i = 1; i < eqExps.size(); i++) {
            System.out.println("<LAndExp>");
            System.out.println(TokenType.printType(TokenType.AND));
            eqExps.get(i).print();
        }
        System.out.println("<LAndExp>");
    }
}
