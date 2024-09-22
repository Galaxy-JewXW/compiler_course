package frontend.syntax.expression;

import frontend.syntax.SyntaxNode;
import frontend.token.Token;

import java.util.ArrayList;

public class RelExp extends SyntaxNode {
    private final ArrayList<AddExp> addExps;
    private final ArrayList<Token> operators;

    public RelExp(ArrayList<AddExp> addExps, ArrayList<Token> operators) {
        this.addExps = addExps;
        this.operators = operators;
    }

    public ArrayList<AddExp> getAddExps() {
        return addExps;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    @Override
    public void print() {
        addExps.get(0).print();
        for (int i = 1; i < addExps.size(); i++) {
            System.out.println("<RelExp>");
            System.out.println(operators.get(i - 1));
            addExps.get(i).print();
        }
        System.out.println("<RelExp>");
    }
}
