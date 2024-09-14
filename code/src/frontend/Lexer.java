package frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
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
    private int pos = 0;
    private final StringBuilder curToken = new StringBuilder();
    private TokenType type = null;
    private int line = 1;

    public Lexer(String inputString) {
        this.inputString = inputString;
    }

    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (hasNext()) {
            tokens.add(new Token(type, curToken.toString(), line));
        }
        return tokens;
    }

    private boolean hasNext() {
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
            getDivOrCmt();
        } else if (c == '<' || c == '>' || c == '=') {
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
            addChar();
        } while (inputString.charAt(pos) != '\"');
        addChar();
    }

    private void getConstChar() {
        type = TokenType.CHRCON;
        do {
            addChar();
        } while (inputString.charAt(pos) != '\'');
        addChar();
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

    private void getAnd() {
        addChar();
        if (inputString.charAt(pos) == '&') {
            addChar();
            type = TokenType.AND;
        } else {
            throw new RuntimeException();
        }
    }

    private void getOr() {
        addChar();
        if (inputString.charAt(pos) == '|') {
            addChar();
            type = TokenType.OR;
        } else {
            throw new RuntimeException();
        }
    }

    private void getDivOrCmt() {
        if (inputString.charAt(pos + 1) == '/') {
            while (inputString.charAt(pos) != '\n') {
                pos++;
            }
            hasNext();
        } else if (inputString.charAt(pos + 1) == '*') {
            pos += 2;
            while (!(inputString.charAt(pos) == '*' && inputString.charAt(pos + 1) == '/')) {
                pos++;
            }
            pos += 2;
            hasNext();
        } else {
            addChar();
            type = TokenType.DIV;
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
