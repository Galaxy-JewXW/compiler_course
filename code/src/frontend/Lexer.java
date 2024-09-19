package frontend;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 词法分析类Lexer，负责分析字符串形式的源程序，将其划分为一个个词法单元，也就是token
 */
public class Lexer {
    /**
     * reservedMap是保留字表
     * 提取出字符串后，若该字符串在保留字表中，则标记为对应属性
     * 否则就标记为标识符
     * <p>
     * singleCharTokens是单符号分隔符表，便于简化程序结构
     */
    private static final Map<String, TokenType> reservedMap = new HashMap<>();
    private static final Map<String, TokenType> singleCharTokens = new HashMap<>();

    static {
        reservedMap.put("main", TokenType.MAINTK);
        reservedMap.put("const", TokenType.CONSTTK);
        reservedMap.put("int", TokenType.INTTK);
        reservedMap.put("char", TokenType.CHARTK);
        reservedMap.put("break", TokenType.BREAKTK);
        reservedMap.put("continue", TokenType.CONTINUETK);
        reservedMap.put("if", TokenType.IFTK);
        reservedMap.put("else", TokenType.ELSETK);
        reservedMap.put("for", TokenType.FORTK);
        reservedMap.put("getint", TokenType.GETINTTK);
        reservedMap.put("getchar", TokenType.GETCHARTK);
        reservedMap.put("printf", TokenType.PRINTFTK);
        reservedMap.put("return", TokenType.RETURNTK);
        reservedMap.put("void", TokenType.VOIDTK);

        singleCharTokens.put("+", TokenType.PLUS);
        singleCharTokens.put("-", TokenType.MINU);
        singleCharTokens.put("*", TokenType.MULT);
        singleCharTokens.put("%", TokenType.MOD);
        singleCharTokens.put(";", TokenType.SEMICN);
        singleCharTokens.put(",", TokenType.COMMA);
        singleCharTokens.put("(", TokenType.LPARENT);
        singleCharTokens.put(")", TokenType.RPARENT);
        singleCharTokens.put("[", TokenType.LBRACK);
        singleCharTokens.put("]", TokenType.RBRACK);
        singleCharTokens.put("{", TokenType.LBRACE);
        singleCharTokens.put("}", TokenType.RBRACE);
    }

    private final String inputString;
    // 指向输入字符串的指针
    private int pos = 0;
    // 当前分析的词法单元
    private final StringBuilder curToken = new StringBuilder();
    // 当前分析的词法单元对应的类别
    private TokenType type = null;
    // 程序行号
    private int line = 1;

    public Lexer(String inputString) {
        this.inputString = inputString;
    }

    // 与主程序的接口，负责产生词法单元
    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (hasNext()) {
            tokens.add(new Token(type, curToken.toString(), line));
        }
        return tokens;
    }

    private boolean hasNext() {
        // 跳过空白字符
        skip();
        if (pos >= inputString.length()) {
            return false;
        }
        curToken.setLength(0);
        type = null;
        char c = inputString.charAt(pos);
        if (isIdHead(c)) {
            while (isIdHead(inputString.charAt(pos)) || Character.isDigit(inputString.charAt(pos))) {
                addChar();
            }
            type = reservedMap.getOrDefault(curToken.toString(), TokenType.IDENFR);
        } else if (Character.isDigit(c)) {
            type = TokenType.INTCON;
            while (Character.isDigit(inputString.charAt(pos))) {
                addChar();
            }
        } else if (c == '\"') {
            getConstString();
        } else if (c == '\'') {
            getConstChar();
        } else if (c == '!') {
            getNot();
        } else if (c == '&') {
            getAnd();
        } else if (c == '|') {
            getOr();
        } else if (singleCharTokens.containsKey(String.valueOf(c))) {
            type = singleCharTokens.get(String.valueOf(c));
            addChar();
        } else if (c == '/') {
            // 判断是除号、单行注释还是多行注释
            return getDivOrCmt();
        } else if (c == '<' || c == '>' || c == '=') {
            // 此时可能是一个符号或两个符号组成单元，需要进一步判断
            getCmpOrAgn(c);
        } else {
            throw new RuntimeException("Unrecognized character: " + c);
        }
        return true;
    }

    private void addChar() {
        curToken.append(inputString.charAt(pos++));
    }

    private void skip() {
        while (pos < inputString.length() && isBlank(inputString.charAt(pos))) {
            pos++;
        }
    }

    private boolean isBlank(char c) {
        if (c == '\n') {
            line++;
            return true;
        } else {
            return c == ' ' || c == '\t' || c == '\r';
        }
    }

    private boolean isIdHead(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private void getConstString() {
        type = TokenType.STRCON;
        do {
            if (inputString.charAt(pos) == '\\') {
                addChar();
            }
            addChar();
        } while (inputString.charAt(pos) != '\"');
        addChar();
    }

    private void getConstChar() {
        type = TokenType.CHRCON;
        addChar(); // 第一个单引号
        if (inputString.charAt(pos) == '\\') {
            addChar();
        }
        addChar();
        addChar(); // 第二个单引号
    }

    private void getNot() {
        addChar();
        if (inputString.charAt(pos) == '=') {
            addChar();
            type = TokenType.NEQ;
        } else {
            type = TokenType.NOT;
        }
    }

    /**
     * 非法符号，指出现了 '&' 和 '|' 这两个符号，应该将其当做 '&&' 与 '||' 进行处理
     * 但是在记录单词名称的时候仍记录'&'和'|'，报错行号为'&'和'|'所在的行号
     */

    private void getAnd() {
        addChar();
        type = TokenType.AND;
        if (inputString.charAt(pos) == '&') {
            addChar();
        } else {
            pos++;
            ErrorHandler.getInstance().addError(
                    new Error(ErrorType.IllegalSymbol, line));
        }
    }

    private void getOr() {
        addChar();
        type = TokenType.OR;
        if (inputString.charAt(pos) == '|') {
            addChar();
        } else {
            pos++;
            ErrorHandler.getInstance().addError(
                    new Error(ErrorType.IllegalSymbol, line));
        }
    }

    private boolean getDivOrCmt() {
        if (inputString.charAt(pos + 1) == '/') {
            while (inputString.charAt(pos) != '\n') {
                pos++;
            }
            return hasNext();
        } else if (inputString.charAt(pos + 1) == '*') {
            pos += 2;
            while (!(inputString.charAt(pos) == '*' && inputString.charAt(pos + 1) == '/')) {
                pos++;
            }
            pos += 2;
            return hasNext();
        } else {
            addChar();
            type = TokenType.DIV;
            return true;
        }
    }

    private void getCmpOrAgn(char ch) {
        addChar();
        if (inputString.charAt(pos) == '=') {
            addChar();
            switch (ch) {
                case '<' -> type = TokenType.LEQ;
                case '>' -> type = TokenType.GEQ;
                case '=' -> type = TokenType.EQL;
                default -> throw new RuntimeException();
            }
        } else {
            switch (ch) {
                case '<' -> type = TokenType.LSS;
                case '>' -> type = TokenType.GRE;
                case '=' -> type = TokenType.ASSIGN;
                default -> throw new RuntimeException();
            }
        }
    }
}
