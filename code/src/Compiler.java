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
        // tokens是已划分好的源程序的词法单元
        Lexer lexer = new Lexer(inputString);
        ArrayList<Token> tokens = lexer.tokenize();
        Printer.printTokens(tokens, lexerOutput);

        // 语法分析部分
        // compUnit是源程序所生成的语法树的根节点
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parse();
        Printer.printCompUnit(compUnit, parserOutput);

        // 异常处理
        if (ErrorHandler.getInstance().hasErrors()) {
            // 在产生异常的情况下，在标准输出和errorOutput上均输出错误信息
            // 并停止后面的分析
            ErrorHandler.getInstance().printErrors(errorOutput);
            return;
        } else {
            // 否则，提示没有产生错误，继续分析
            System.out.println("No errors found");
        }
    }
}
