package frontend.syntax;

import frontend.Token;
import frontend.TokenType;
import frontend.syntax.expression.ConstExp;

import java.util.ArrayList;

// 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
// 1.常表达式初值 2.一维数组初值 3.字符串赋值
public class ConstInitVal extends SyntaxNode {
    private final ConstExp constExp;
    private final ArrayList<ConstExp> constExps;
    private final Token stringConst;

    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
        this.constExps = null;
        this.stringConst = null;
    }

    public ConstInitVal(ArrayList<ConstExp> constExps) {
        this.constExp = null;
        this.constExps = constExps;
        this.stringConst = null;
    }

    public ConstInitVal(Token stringConst) {
        this.constExp = null;
        this.constExps = null;
        this.stringConst = stringConst;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public Token getStringConst() {
        return stringConst;
    }

    @Override
    public void print() {
        if (constExp != null) {
            constExp.print();
        } else if (stringConst != null) {
            System.out.println(stringConst);
        } else {
            System.out.println(TokenType.printType(TokenType.LBRACE));
            if (constExps != null) {
                for (int i = 0; i < constExps.size(); i++) {
                    if (i > 0) {
                        System.out.println(TokenType.printType(TokenType.COMMA));
                    }
                    constExps.get(i).print();
                }
            }
            System.out.println(TokenType.printType(TokenType.RBRACE));
        }
        System.out.println("<ConstInitVal>");
    }
}
