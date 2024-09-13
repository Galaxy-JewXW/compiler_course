package syntax.expression;

import frontend.Token;

import java.util.ArrayList;

// 加减表达式 AddExp -> MulExp | AddExp ('+' | '−') MulExp
public class AddExp {
    private final ArrayList<MulExp> mulExps;
    private final ArrayList<Token> ops;

    public AddExp(ArrayList<MulExp> mulExps, ArrayList<Token> ops) {
        this.mulExps = mulExps;
        this.ops = ops;
    }

    public ArrayList<MulExp> getMulExps() {
        return mulExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void output() {
        mulExps.get(0).output();
        for (int i = 1; i < mulExps.size(); i++) {
            System.out.println("<AddExp>");
            System.out.println(ops.get(i - 1));
            mulExps.get(i).output();
        }
        System.out.println("<AddExp>");
    }

    public void check() {
        for (MulExp mulExp : mulExps) {
            mulExp.check();
        }
    }
}
