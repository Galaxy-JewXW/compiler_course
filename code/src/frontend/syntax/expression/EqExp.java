package frontend.syntax.expression;

import frontend.Token;
import frontend.syntax.SyntaxNode;

import java.util.ArrayList;

public class EqExp extends SyntaxNode {
    private final ArrayList<RelExp> relExps;
    private final ArrayList<Token> operators;

    public EqExp(ArrayList<RelExp> relExps, ArrayList<Token> operators) {
        this.relExps = relExps;
        this.operators = operators;
    }

    public ArrayList<RelExp> getRelExps() {
        return relExps;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    @Override
    public void print() {
        relExps.get(0).print();
        for (int i = 1; i < relExps.size(); i++) {
            System.out.println("<EqExp>");
            System.out.println(operators.get(i - 1));
            relExps.get(i).print();
        }
        System.out.println("<EqExp>");
    }
}
