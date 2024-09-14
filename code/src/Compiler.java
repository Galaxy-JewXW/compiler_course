import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.Token;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String lexerOutput = "lexer.txt";
    private static final String errorOutput = "error.txt";

    public static void main(String[] args) throws Exception {
        String inputString = Files.readString(Paths.get(inputFile));

        // 词法分析
        Lexer lexer = new Lexer(inputString);
        ArrayList<Token> tokens = lexer.tokenize();
        PrintStream origin = System.out;
        System.setOut(new PrintStream(lexerOutput));
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.setOut(origin);

        // 语法分析
        Parser parser = new Parser(tokens);

        // 异常处理
        if (ErrorHandler.getInstance().hasErrors()) {
            ErrorHandler.getInstance().printErrors(errorOutput);
            return;
        }
    }
}
