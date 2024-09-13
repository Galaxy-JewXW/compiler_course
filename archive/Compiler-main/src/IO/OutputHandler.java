package IO;

import config.Config;
import error.ErrorNode;
import token.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class OutputHandler {
    public static void println(String path, String content) {
        File output = new File(path);
        try (FileWriter fileWriter = new FileWriter(output, true)) {
            fileWriter.write(content + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //默认输出到outfile中
    public static void println(String content) {
        println(Config.outputPath, content);
    }

    public static void logError(String content) {
        println(Config.errorPath, content);
    }

    public static void logError(ErrorNode error) {
        logError(error.toString());
    }

    public static void genLLVM(String inst) {
        println(Config.outLLVM, inst);
    }

    public static void genMIPS(String inst) {
        println(Config.mipsPath, inst);
    }

    public static void printToken(Token token) {
        println(token.toString());
    }

    /* 根据TokenType来得到固定对应的token
     * 注释error的部分不应当通过本函数来实现而应当通过上一个函数来实现*/
    public static void printToken(TokenType tokenType) {
        String token = tokenType.toString() + ' ';
        switch (tokenType) {
            case COMMA -> token += ",";
            case IDENFR -> token += "Ident";//error
            case INTCON -> token += "IntConst";//error
            case STRCON -> token += "FormatString";//error
            case MAINTK -> token += "main";
            case CONSTTK -> token += "const";
            case INTTK -> token += "int";
            case BREAKTK -> token += "break";
            case CONTINUETK -> token += "continue";
            case IFTK -> token += "if";
            case ELSETK -> token += "else";
            case NOT -> token += "!";
            case AND -> token += "&&";
            case OR -> token += "||";
            case FORTK -> token += "for";
            case GETINTTK -> token += "getint";
            case PRINTFTK -> token += "printf";
            case RETURNTK -> token += "return";
            case PLUS -> token += "+";
            case MINU -> token += "-";
            case VOIDTK -> token += "void";
            case MULT -> token += "*";
            case DIV -> token += "/";
            case MOD -> token += "%";
            case LSS -> token += "<";
            case LEQ -> token += "<=";
            case GRE -> token += ">";
            case GEQ -> token += ">=";
            case EQL -> token += "==";
            case NEQ -> token += "!=";
            case ASSIGN -> token += "=";
            case SEMICN -> token += ";";
            case LPARENT -> token += "(";
            case RPARENT -> token += ")";
            case LBRACK -> token += "[";
            case RBRACK -> token += "]";
            case LBRACE -> token += "{";
            case RBRACE -> token += "}";
        }
        println(token);
    }

}
