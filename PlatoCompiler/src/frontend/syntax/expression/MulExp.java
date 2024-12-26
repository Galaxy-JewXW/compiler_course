package frontend.syntax.expression;

import frontend.syntax.Calculable;
import frontend.syntax.SyntaxNode;
import frontend.token.Token;

import java.util.ArrayList;

public class MulExp extends SyntaxNode implements Calculable {
    private final ArrayList<UnaryExp> unaryExps;
    private final ArrayList<Token> operators;

    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<Token> operators) {
        this.unaryExps = unaryExps;
        this.operators = operators;
    }

    public ArrayList<UnaryExp> getUnaryExps() {
        return unaryExps;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    @Override
    public void print() {
        unaryExps.get(0).print();
        for (int i = 1; i < unaryExps.size(); i++) {
            System.out.println("<MulExp>");
            System.out.println(operators.get(i - 1));
            unaryExps.get(i).print();
        }
        System.out.println("<MulExp>");
    }

    @Override
    public int calculate() {
        int ans = unaryExps.get(0).calculate();
        for (int i = 1; i < unaryExps.size(); i++) {
            ans = switch (operators.get(i - 1).getType()) {
                case MULT -> ans * unaryExps.get(i).calculate();
                case DIV -> ans / unaryExps.get(i).calculate();
                case MOD -> ans % unaryExps.get(i).calculate();
                case FUCK -> getFuck(ans, unaryExps.get(i).calculate());
                default -> throw new RuntimeException("Shouldn't reach here");
            };
        }
        return ans;
    }

    public int getFuck(int a, int b) {
        int ans = 1;
        for (int i = 0; i < b; i++) {
            ans = ans * (a + b);
        }
        return ans;
    }
}
