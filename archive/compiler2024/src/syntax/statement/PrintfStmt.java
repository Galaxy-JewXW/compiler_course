package syntax.statement;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.Token;
import frontend.TokenType;
import syntax.expression.Exp;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintfStmt implements Stmt {
    private final Token token;
    private final Token format;
    private final ArrayList<Exp> exps;

    public PrintfStmt(Token token, Token format, ArrayList<Exp> exps) {
        this.token = token;
        this.format = format;
        this.exps = exps;
    }

    public Token getFormat() {
        return format;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public void output() {
        System.out.println(token);
        System.out.println(TokenType.LPARENT.name() + " " + TokenType.LPARENT);
        System.out.println(format);
        for (Exp exp : exps) {
            System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
            exp.output();
        }
        System.out.println(TokenType.RPARENT.name() + " " + TokenType.RPARENT);
        System.out.println(TokenType.SEMICN.name() + " " + TokenType.SEMICN);
        System.out.println("<Stmt>");
    }

    @Override
    public void check() {
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        if (!validFormat(format.getValue())) {
            errorVisitor.addError(new ErrorLog(ErrorType.IllegalChar,
                    format.getLine()));
        }
        if (formatCount(format.getValue()) != exps.size()) {
            errorVisitor.addError(new ErrorLog(ErrorType.PrintfFormatStrNumNotMatch,
                    token.getLine()));
        }
    }

    private boolean validFormat(String format) {
        for (int i = 1; i < format.length() - 1; i++) {
            char c = format.charAt(i);
            if (c == '%') {
                if (format.charAt(i + 1) != 'd') {
                    return false;
                } else {
                    i++;
                }
            } else if (c == ' ' || c == '!' || (c >= '(' && c <= '~')) {
                if (c == '\\') {
                    if (format.charAt(i + 1) != 'n') {
                        return false;
                    } else {
                        i++;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private int formatCount(String format) {
        int res = 0;
        Pattern pattern = Pattern.compile("%d");
        Matcher matcher = pattern.matcher(format);
        while (matcher.find()) {
            res++;
        }
        return res;
    }
}
