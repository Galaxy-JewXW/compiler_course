package frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    private final BufferedReader input;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int curPos = 0;
    private String curLine = "";
    private boolean inMultiComment = false;
    public Lexer(BufferedReader input) {
        this.input = input;
    }

    public ArrayList<Token> getTokens() {
        return new ArrayList<>(tokens);
    }

    public void tokenize() throws IOException {
        int line = 1;
        while ((curLine = input.readLine()) != null) {
            curPos = 0;
            while (curPos < curLine.length()) {
                Token token = nextToken(line);
                if (token.getType() == TokenType.SINGLE_LINE_COMMENT
                        || token.getType() == TokenType.EMPTY_LINE) {
                    break;
                }
                if (token.getType() != TokenType.MULTI_LINE_COMMENT) {
                    tokens.add(token);
                }
            }
            line++;
        }
        input.close();
    }

    public Token nextToken(int lineNum) {
        StringBuilder sb = new StringBuilder();
        while (curPos < curLine.length() && Character.isWhitespace(curLine.charAt(curPos))) {
            curPos++;
        }
        if (curPos >= curLine.length()) {
            return new Token(TokenType.EMPTY_LINE, "", lineNum);
        }

        if (inMultiComment) {
            return dealMultiComment();
        }

        if (Character.isLetter(curLine.charAt(curPos)) || curLine.charAt(curPos) == '_') {
            while (Character.isLetterOrDigit(curLine.charAt(curPos)) || curLine.charAt(curPos) == '_') {
                sb.append(curLine.charAt(curPos));
                curPos++;
                if (curPos >= curLine.length()) {
                    break;
                }
            }
            return keyword2Token(sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '\"') {
            do {
                sb.append(curLine.charAt(curPos));
                curPos++;
            } while (curLine.charAt(curPos) != '\"');
            sb.append(curLine.charAt(curPos));
            curPos++;
            return new Token(TokenType.STRCON, sb.toString(), lineNum);
        } else if (Character.isDigit(curLine.charAt(curPos))) {
            do {
                sb.append(curLine.charAt(curPos));
                curPos++;
            } while (curPos < curLine.length() && Character.isDigit(curLine.charAt(curPos)));
            return new Token(TokenType.INTCON, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '!') {
            sb.append(curLine.charAt(curPos++));
            if (curLine.charAt(curPos) == '=') {
                sb.append(curLine.charAt(curPos));
                curPos++;
                return new Token(TokenType.NEQ, sb.toString(), lineNum);
            }
            else {
                return new Token(TokenType.NOT, sb.toString(), lineNum);
            }
        } else if (curLine.charAt(curPos) == '/') {
            if (curLine.charAt(curPos + 1) == '/') {
                return new Token(TokenType.SINGLE_LINE_COMMENT, "//", lineNum);
            } else if (curLine.charAt(curPos + 1) == '*') {
                curPos += 2;
                while (true) {
                    while (curPos < curLine.length() && curLine.charAt(curPos) != '*') {
                        curPos++;
                    }
                    if (curPos >= curLine.length()) {
                        inMultiComment = true;
                        return new Token(TokenType.MULTI_LINE_COMMENT, "", lineNum);
                    } else if (curLine.charAt(curPos + 1) == '/') {
                        inMultiComment = false;
                        curPos += 2;
                        return new Token(TokenType.MULTI_LINE_COMMENT, "", lineNum);
                    } else {
                        curPos++;
                    }
                }
            } else {
                sb.append(curLine.charAt(curPos));
                curPos++;
                return new Token(TokenType.DIV, sb.toString(), lineNum);
            }
        } else {
            return parseOperator(lineNum);
        }
    }

    private Token dealMultiComment() {
        while (true) {
            while (curPos < curLine.length() && curLine.charAt(curPos) != '*') {
                curPos++;
            }
            if (curPos >= curLine.length()) {
                return new Token(TokenType.MULTI_LINE_COMMENT, "", -1);
            } else if (curLine.charAt(curPos + 1) == '/') {
                inMultiComment = false;
                curPos += 2;
                return new Token(TokenType.MULTI_LINE_COMMENT, "", -1);
            } else {
                curPos++;
            }
        }
    }

    private Token keyword2Token(String string, int lineNum) {
        return switch (string) {
            case "main" -> new Token(TokenType.MAINTK, "main", lineNum);
            case "const" -> new Token(TokenType.CONSTTK, "const", lineNum);
            case "int" -> new Token(TokenType.INTTK, "int", lineNum);
            case "break" -> new Token(TokenType.BREAKTK, "break", lineNum);
            case "continue" -> new Token(TokenType.CONTINUETK, "continue", lineNum);
            case "if" -> new Token(TokenType.IFTK, "if", lineNum);
            case "else" -> new Token(TokenType.ELSETK, "else", lineNum);
            case "for" -> new Token(TokenType.FORTK, "for", lineNum);
            case "getint" -> new Token(TokenType.GETINTTK, "getint", lineNum);
            case "printf" -> new Token(TokenType.PRINTFTK, "printf", lineNum);
            case "return" -> new Token(TokenType.RETURNTK, "return", lineNum);
            case "void" -> new Token(TokenType.VOIDTK, "void", lineNum);
            default -> new Token(TokenType.IDENFR, string, lineNum);
        };
    }

    private Token parseOperator(int lineNum) {
        StringBuilder sb = new StringBuilder();
        if (curLine.charAt(curPos) == '&' &&
                curLine.charAt(curPos + 1) == '&') {
            sb.append("&&");
            curPos += 2;
            return new Token(TokenType.AND, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '|' &&
                curLine.charAt(curPos + 1) == '|') {
            sb.append("||");
            curPos += 2;
            return new Token(TokenType.OR, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '+') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.PLUS, "+", lineNum);
        } else if (curLine.charAt(curPos) == '-') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.MINU, "-", lineNum);
        } else if (curLine.charAt(curPos) == '*') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.MULT, "*", lineNum);
        } else if (curLine.charAt(curPos) == '%') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.MOD, "%", lineNum);
        } else if (curLine.charAt(curPos) == '<') {
            sb.append(curLine.charAt(curPos++));
            if (curLine.charAt(curPos) == '=') {
                sb.append(curLine.charAt(curPos++));
                return new Token(TokenType.LEQ, sb.toString(), lineNum);
            } else {
                return new Token(TokenType.LSS, sb.toString(), lineNum);
            }
        } else if (curLine.charAt(curPos) == '>') {
            sb.append(curLine.charAt(curPos++));
            if (curLine.charAt(curPos) == '=') {
                sb.append(curLine.charAt(curPos++));
                return new Token(TokenType.GEQ, sb.toString(), lineNum);
            } else {
                return new Token(TokenType.GRE, sb.toString(), lineNum);
            }
        } else if (curLine.charAt(curPos) == '=') {
            sb.append(curLine.charAt(curPos++));
            if (curLine.charAt(curPos) == '=') {
                sb.append(curLine.charAt(curPos++));
                return new Token(TokenType.EQL, sb.toString(), lineNum);
            } else {
                return new Token(TokenType.ASSIGN, sb.toString(), lineNum);
            }
        } else if (curLine.charAt(curPos) == ';') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.SEMICN, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == ',') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.COMMA, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '(') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.LPARENT, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == ')') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.RPARENT, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '[') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.LBRACK, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == ']') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.RBRACK, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '{') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.LBRACE, sb.toString(), lineNum);
        } else if (curLine.charAt(curPos) == '}') {
            sb.append(curLine.charAt(curPos++));
            return new Token(TokenType.RBRACE, sb.toString(), lineNum);
        }
        return new Token(TokenType.EMPTY_LINE, "", lineNum);
    }
}
