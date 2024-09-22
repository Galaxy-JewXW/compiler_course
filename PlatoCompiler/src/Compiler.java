import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.Visitor;
import frontend.syntax.CompUnit;
import frontend.token.Token;
import tools.Printer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String lexerOutput = "lexer.txt";
    private static final String parserOutput = "parser.txt";
    private static final String errorOutput = "error.txt";
    private static final String llvmOutput = "llvm_ir.txt";
    private static final String irOutput = "ir.txt"; // 优化后的中间代码

    public static void main(String[] args) throws Exception {
        String inputString = Files.readString(Paths.get(inputFile));

        // 词法分析
        // tokens是已划分好的源程序的词法单元
        Lexer lexer = new Lexer(inputString);
        ArrayList<Token> tokens = lexer.tokenize();
        Printer.printTokens(tokens, lexerOutput);
        System.out.println("lexer complete.");

        // 语法分析部分
        // compUnit是源程序所生成的语法树的根节点
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parse();
        Printer.printCompUnit(compUnit, parserOutput);
        System.out.println("parser complete.");

        // 语义分析，建立符号表
        Visitor visitor = new Visitor(compUnit);
        visitor.build();

        // 异常处理
        Printer.printErrors(ErrorHandler.getInstance().getErrors(), errorOutput);
        if (!ErrorHandler.getInstance().getErrors().isEmpty()) {
            return;
        }
    }
}
