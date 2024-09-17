package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;
import frontend.token.TokenType;

import java.util.ArrayList;

public class LOrExp extends SyntaxNode {
    private final ArrayList<LAndExp> lAndExps;

    public LOrExp(ArrayList<LAndExp> lAndExps) {
        this.lAndExps = lAndExps;
    }

    public ArrayList<LAndExp> getlAndExps() {
        return lAndExps;
    }

    @Override
    public void print() {
        lAndExps.get(0).print();
        for (int i = 1; i < lAndExps.size(); i++) {
            System.out.println("<LOrExp>");
            System.out.println(TokenType.printType(TokenType.OR));
            lAndExps.get(i).print();
        }
        System.out.println("<LOrExp>");
    }
}
