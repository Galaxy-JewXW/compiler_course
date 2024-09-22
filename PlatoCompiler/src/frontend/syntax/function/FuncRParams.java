package frontend.syntax.function;

import frontend.syntax.SyntaxNode;
import frontend.syntax.expression.Exp;
import frontend.token.TokenType;

import java.util.ArrayList;

// 函数实参表 FuncRParams → Exp { ',' Exp }
public class FuncRParams extends SyntaxNode {
    private final ArrayList<Exp> exps;

    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public void print() {
        for (int i = 0; i < exps.size(); i++) {
            if (i > 0) {
                System.out.print(TokenType.printType(TokenType.COMMA));
            }
            exps.get(i).print();
        }
        System.out.println("<FuncRParams>");
    }
}
