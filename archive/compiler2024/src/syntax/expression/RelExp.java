package syntax.expression;

import frontend.Token;

import java.util.ArrayList;

// 关系表达式 RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
public class RelExp {
    private final ArrayList<AddExp> addExps;
    private final ArrayList<Token> ops;

    public RelExp(ArrayList<AddExp> addExps, ArrayList<Token> ops) {
        this.addExps = addExps;
        this.ops = ops;
    }

    public ArrayList<AddExp> getAddExps() {
        return addExps;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }

    public void output() {
        addExps.get(0).output();
        for (int i = 1; i < addExps.size(); i++) {
            System.out.println("<RelExp>");
            System.out.println(ops.get(i - 1));
            addExps.get(i).output();
        }
        System.out.println("<RelExp>");
    }

    public void check() {
        for (AddExp addExp : addExps) {
            addExp.check();
        }
    }
}
