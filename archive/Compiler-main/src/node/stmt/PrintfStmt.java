package node.stmt;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.expression.Exp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class PrintfStmt implements Stmt {
    //| 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
    private final Token printfToken;
    private final Token formatString;
    private final ArrayList<Exp> exps;

    public PrintfStmt(Token printfToken, Token formatString, ArrayList<Exp> exps) {
        this.printfToken = printfToken;
        this.formatString = formatString;
        this.exps = exps;
    }

    public Token getFormatString() {
        return formatString;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.PRINTFTK);
        OutputHandler.printToken(TokenType.LPARENT);
        OutputHandler.printToken(formatString);
        for (Exp exp : exps) {
            OutputHandler.printToken(TokenType.COMMA);
            exp.print();
        }
        OutputHandler.printToken(TokenType.RPARENT);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }

    @Override
    public void checkError() {
        if (!isValidFormat(formatString.getToken())) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.a, formatString.getLine()));
        }
        if (numOfFormatChar(formatString.getToken()) != exps.size()) {
            ErrorHandler.getInstance().addError(
                    new ErrorNode(ErrorType.l, printfToken.getLine()));
        }
    }

    private boolean isValidFormat(String format) {
        for (int i = 1; i < format.length() - 1; i++) {//formatString 首尾都是'\"'
            char c = format.charAt(i);
            if (c == '%') {
                if (format.charAt(++i) != 'd') {
                    return false;
                }
            } else if (c == 32 || c == 33 || (c >= 40 && c <= 126)) {
                if (c == '\\') {
                    if (format.charAt(++i) != 'n') {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private int numOfFormatChar(String format) {
        int res = 0;
        for (int i = 0; i < format.length(); i++) {
            if (format.charAt(i) == '%' && format.charAt(i + 1) == 'd') {
                res++;
            }
        }
        return res;
    }
}
