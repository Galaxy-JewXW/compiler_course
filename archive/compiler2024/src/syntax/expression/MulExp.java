package syntax.expression;

import frontend.Token;

import java.util.ArrayList;

// 乘除模表达式 MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
public class MulExp {
    private final ArrayList<UnaryExp> unaryExps;
    private final ArrayList<Token> ops;

    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<Token> ops) {
        this.unaryExps = unaryExps;
        this.ops = ops;
    }

    public ArrayList<UnaryExp> getUnaryExps() {
        return unaryExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void output() {
        unaryExps.get(0).output();
        for (int i = 1; i < unaryExps.size(); i++) {
            System.out.println("<MulExp>");
            System.out.println(ops.get(i - 1));
            unaryExps.get(i).output();
        }
        System.out.println("<MulExp>");
    }

    public void check() {
        for (UnaryExp unaryExp : unaryExps) {
            unaryExp.check();
        }
    }
}
