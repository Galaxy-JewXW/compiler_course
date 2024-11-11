package frontend.syntax.expression;

import frontend.syntax.Calculable;
import frontend.syntax.Character;
import frontend.syntax.LVal;
import frontend.syntax.Number;
import frontend.syntax.SyntaxNode;
import frontend.token.TokenType;

// 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character// 四种情况均需覆盖
public class PrimaryExp extends SyntaxNode implements Calculable {
    private final Exp exp;
    private final LVal lVal;
    private final Number number;
    private final Character character;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
        this.lVal = null;
        this.number = null;
        this.character = null;
    }

    public PrimaryExp(LVal lVal) {
        this.exp = null;
        this.lVal = lVal;
        this.number = null;
        this.character = null;
    }

    public PrimaryExp(Number number) {
        this.exp = null;
        this.lVal = null;
        this.number = number;
        this.character = null;
    }

    public PrimaryExp(Character character) {
        this.exp = null;
        this.lVal = null;
        this.number = null;
        this.character = character;
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getLVal() {
        return lVal;
    }

    public Number getNumber() {
        return number;
    }

    public Character getCharacter() {
        return character;
    }

    @Override
    public void print() {
        if (exp != null) {
            System.out.println(TokenType.printType(TokenType.LPARENT));
            exp.print();
            System.out.println(TokenType.printType(TokenType.RPARENT));
        } else if (lVal != null) {
            lVal.print();
        } else if (number != null) {
            number.print();
        } else if (character != null) {
            character.print();
        }
        System.out.println("<PrimaryExp>");
    }

    @Override
    public int calculate() {
        int ans;
        if (exp != null) {
            ans = exp.calculate();
        } else if (lVal != null) {
            ans = lVal.calculate();
        } else if (number != null) {
            ans = number.calculate();
        } else if (character != null) {
            ans = character.calculate();
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
        return ans;
    }
}
