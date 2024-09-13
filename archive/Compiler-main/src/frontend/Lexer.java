package frontend;

import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class Lexer {
    private final String in;
    private int pos;
    private StringBuilder curToken;
    private TokenType symbol;
    private int line;

    public Lexer(String in) {
        this.in = in;
        pos = 0;
        line = 1;
        curToken = null;
        symbol = null;
    }

    private boolean hasNext() {
        curToken = new StringBuilder();
        symbol = null;
        while (pos < in.length() && isBlank(in.charAt(pos))) {
            pos++;
        }
        if (pos >= in.length()) {
            return false;
        }
        if (isIdHead(in.charAt(pos))) {
            while (isIdHead(in.charAt(pos)) || Character.isDigit(in.charAt(pos))) {
                catToken();
            }
            reserve();
        } else if (Character.isDigit(in.charAt(pos))) {
            symbol = TokenType.INTCON;
            while (Character.isDigit(in.charAt(pos))) {
                catToken();
            }
        } else if (in.charAt(pos) == '"') {
            symbol = TokenType.STRCON;
            catToken();
            while (in.charAt(pos) != '"') {
                catToken();
            }
            catToken();
        } else if (in.charAt(pos) == '!') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = TokenType.NEQ;
            } else {
                symbol = TokenType.NOT;
            }
        } else if (in.charAt(pos) == '&') {
            catToken();
            if (in.charAt(pos) == '&') {
                catToken();
                symbol = TokenType.AND;
            }
            //else error();
        } else if (in.charAt(pos) == '|') {
            catToken();
            if (in.charAt(pos) == '|') {
                catToken();
                symbol = TokenType.OR;
            }
            //else error();
        } else if (in.charAt(pos) == '+') {
            catToken();
            symbol = TokenType.PLUS;
        } else if (in.charAt(pos) == '-') {
            catToken();
            symbol = TokenType.MINU;
        } else if (in.charAt(pos) == '*') {
            catToken();
            symbol = TokenType.MULT;
        } else if (in.charAt(pos) == '/') {
            if (in.charAt(pos + 1) == '/') {
                while (in.charAt(pos) != '\n') {
                    pos++;
                }
                return hasNext();
            } else if (in.charAt(pos + 1) == '*') {
                while (!(in.charAt(pos) == '*' && in.charAt(pos + 1) == '/')) {
                    pos++;
                }
                pos += 2;
                return hasNext();
            } else {
                catToken();
                symbol = TokenType.DIV;
            }
        } else if (in.charAt(pos) == '%') {
            catToken();
            symbol = TokenType.MOD;
        } else if (in.charAt(pos) == '<') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = TokenType.LEQ;
            } else {
                symbol = TokenType.LSS;
            }
        } else if (in.charAt(pos) == '>') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = TokenType.GEQ;
            } else {
                symbol = TokenType.GRE;
            }
        } else if (in.charAt(pos) == '=') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = TokenType.EQL;
            } else {
                symbol = TokenType.ASSIGN;
            }
        } else if (in.charAt(pos) == ';') {
            catToken();
            symbol = TokenType.SEMICN;
        } else if (in.charAt(pos) == ',') {
            catToken();
            symbol = TokenType.COMMA;
        } else if (in.charAt(pos) == '(') {
            catToken();
            symbol = TokenType.LPARENT;
        } else if (in.charAt(pos) == ')') {
            catToken();
            symbol = TokenType.RPARENT;
        } else if (in.charAt(pos) == '[') {
            catToken();
            symbol = TokenType.LBRACK;
        } else if (in.charAt(pos) == ']') {
            catToken();
            symbol = TokenType.RBRACK;
        } else if (in.charAt(pos) == '{') {
            catToken();
            symbol = TokenType.LBRACE;
        } else if (in.charAt(pos) == '}') {
            catToken();
            symbol = TokenType.RBRACE;
        }
        return true;
    }

    public ArrayList<Token> getTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (hasNext()) {
            tokens.add(new Token(symbol, curToken.toString(), line));
        }
        return tokens;
    }

    private void catToken() {
        curToken.append(in.charAt(pos));
        pos++;
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
        String tmp = curToken.toString();
        switch (tmp) {
            case "main" -> symbol = TokenType.MAINTK;
            case "const" -> symbol = TokenType.CONSTTK;
            case "int" -> symbol = TokenType.INTTK;
            case "break" -> symbol = TokenType.BREAKTK;
            case "continue" -> symbol = TokenType.CONTINUETK;
            case "if" -> symbol = TokenType.IFTK;
            case "else" -> symbol = TokenType.ELSETK;
            case "for" -> symbol = TokenType.FORTK;
            case "getint" -> symbol = TokenType.GETINTTK;
            case "printf" -> symbol = TokenType.PRINTFTK;
            case "return" -> symbol = TokenType.RETURNTK;
            case "void" -> symbol = TokenType.VOIDTK;
            default -> symbol = TokenType.IDENFR;
        }
    }

}
