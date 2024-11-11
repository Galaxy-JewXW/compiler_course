package frontend.syntax.expression;

import frontend.syntax.Calculable;
import frontend.syntax.SyntaxNode;
import frontend.token.Token;

import java.util.ArrayList;

public class AddExp extends SyntaxNode implements Calculable {
    private final ArrayList<MulExp> mulExps;
    private final ArrayList<Token> operators;

    public AddExp(ArrayList<MulExp> mulExps, ArrayList<Token> operators) {
        this.mulExps = mulExps;
        this.operators = operators;
    }

    public ArrayList<MulExp> getMulExps() {
        return mulExps;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    @Override
    public void print() {
        mulExps.get(0).print();
        for (int i = 1; i < mulExps.size(); i++) {
            System.out.println("<AddExp>");
            System.out.println(operators.get(i - 1));
            mulExps.get(i).print();
        }
        System.out.println("<AddExp>");
    }

    @Override
    public int calculate() {
        int ans = mulExps.get(0).calculate();
        for (int i = 1; i < mulExps.size(); i++) {
            ans = switch (operators.get(i - 1).getType()) {
                case PLUS -> ans + mulExps.get(i).calculate();
                case MINU -> ans - mulExps.get(i).calculate();
                default -> throw new RuntimeException("Shouldn't reach here");
            };
        }
        return ans;
    }
}
