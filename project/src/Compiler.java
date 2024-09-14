import frontend.Lexer;
import frontend.Token;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String lexerOutputFile = "lexer.txt";

    public static void main(String[] args) throws Exception {
        String inputString = Files.readString(Paths.get(inputFile));
        Lexer lexer = new Lexer(inputString);
        ArrayList<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
