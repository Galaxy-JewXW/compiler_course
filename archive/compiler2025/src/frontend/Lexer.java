package frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final String inputString;
    private int pos = 0;
    private final StringBuilder curToken = new StringBuilder();
    private TokenType type = null;
    private int line = 1;

    private static final Map<String, TokenType> reservedMap = new HashMap<>();
    private static final Map<String, TokenType> singleCharTokens = new HashMap<>();

    static {
        reservedMap.put("main", TokenType.MAINTK);
        reservedMap.put("const", TokenType.CONSTTK);
        reservedMap.put("int", TokenType.INTTK);
        reservedMap.put("break", TokenType.BREAKTK);
        reservedMap.put("continue", TokenType.CONTINUETK);
        reservedMap.put("if", TokenType.IFTK);
        reservedMap.put("else", TokenType.ELSETK);
        reservedMap.put("for", TokenType.FORTK);
        reservedMap.put("getint", TokenType.GETINTTK);
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

    public Lexer(String inputString) {
        this.inputString = inputString;
    }

    private boolean hasNext() {
        skipBlanks();
        if (pos >= inputString.length()) {
            return false;
        }
        curToken.setLength(0);
        type = null;
        char curChar = inputString.charAt(pos);
        if (isIdHead(curChar)) {
            while (isIdHead(inputString.charAt(pos)) || Character.isDigit(inputString.charAt(pos))) {
                addChar();
            }
            reserve();
        } else if (Character.isDigit(curChar)) {
            type = TokenType.INTCON;
            while (Character.isDigit(inputString.charAt(pos))) {
                addChar();
            }
        } else if (curChar == '"') {
            parseConstString();
        } else if (curChar == '!') {
            parseNotEqual();
        } else if (curChar == '&') {
            parseAnd();
        } else if (curChar == '|') {
            parseOr();
        } else if (singleCharTokens.containsKey(String.valueOf(curChar))) {
            type = singleCharTokens.get(String.valueOf(curChar));
            addChar();
        } else if (curChar == '/') {
            parseCommentOrDiv();
        } else if (curChar == '<' || curChar == '>' || curChar == '=') {
            parseComparisonOrAssign(curChar);
        } else {
            throw new RuntimeException();
        }
        return true;
    }

    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (hasNext()) {
            tokens.add(new Token(type, curToken.toString(), line));
        }
        return tokens;
    }

    private void addChar() {
        curToken.append(inputString.charAt(pos));
        pos++;
    }

    private void skipBlanks() {
        while (pos < inputString.length() && isBlank(inputString.charAt(pos))) {
            pos++;
        }
    }

    private boolean isBlank(char c) {
        if (c == '\n') {
            line++;
            return true;
        }
        return c == ' ' || c == '\t' || c == '\r';
    }

    private boolean isIdHead(char c) {
        return c == '_' || Character.isLetter(c);
    }

    private void reserve() {
        String tokenString = curToken.toString();
        type = reservedMap.getOrDefault(tokenString, TokenType.IDENFR);
    }

    private void parseConstString() {
        type = TokenType.STRCON;
        addChar();
        while (inputString.charAt(pos) != '\"') {
            addChar();
        }
        addChar();
    }

    private void parseNotEqual() {
        addChar();
        if (inputString.charAt(pos) == '=') {
            addChar();
            type = TokenType.NEQ;
        } else {
            type = TokenType.NOT;
        }
    }

    private void parseAnd() {
        addChar();
        if (inputString.charAt(pos) == '&') {
            addChar();
            type = TokenType.AND;
        } else {
            throw new RuntimeException();
        }
    }

    private void parseOr() {
        addChar();
        if (inputString.charAt(pos) == '|') {
            addChar();
            type = TokenType.OR;
        } else {
            throw new RuntimeException();
        }
    }

    private void parseCommentOrDiv() {
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

    private void parseComparisonOrAssign(char currentChar) {
        addChar();
        if (inputString.charAt(pos) == '=') {
            addChar();
            switch (currentChar) {
                case '<' -> type = TokenType.LEQ;
                case '>' -> type = TokenType.GEQ;
                case '=' -> type = TokenType.EQL;
                default -> throw new RuntimeException();
            }
        } else {
            switch (currentChar) {
                case '<' -> type = TokenType.LSS;
                case '>' -> type = TokenType.GRE;
                case '=' -> type = TokenType.ASSIGN;
                default -> throw new RuntimeException();
            }
        }
    }
}
