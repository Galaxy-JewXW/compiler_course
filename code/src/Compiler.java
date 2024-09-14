import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.Token;
import frontend.syntax.CompUnit;
import tools.Printer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String lexerOutput = "lexer.txt";
    private static final String parserOutput = "parser.txt";
    private static final String errorOutput = "error.txt";

    public static void main(String[] args) throws Exception {
        String inputString = Files.readString(Paths.get(inputFile));

        // 词法分析
        Lexer lexer = new Lexer(inputString);
        ArrayList<Token> tokens = lexer.tokenize();
        Printer.printTokens(tokens, lexerOutput);

        // 语法分析
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parse();
        Printer.printCompUnit(compUnit, parserOutput);

        // 异常处理
        if (ErrorHandler.getInstance().hasErrors()) {
            ErrorHandler.getInstance().printErrors(errorOutput);
            return;
        } else {
            System.out.println("No errors found");
        }
    }
}
