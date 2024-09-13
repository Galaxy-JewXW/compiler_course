package syntax;

import frontend.TokenType;
import syntax.expression.Exp;

import java.util.ArrayList;

// 函数实参表 FuncRParams -> Exp { ',' Exp }
public class FuncRParams {
    private final ArrayList<Exp> exps;

    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public void output() {
        exps.get(0).output();
        for (int i = 1; i < exps.size(); i++) {
            System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
            exps.get(i).output();
        }
        System.out.println("<FuncRParams>");
    }

    public void check() {
        for (Exp exp : exps) {
            exp.check();
        }
    }
}
