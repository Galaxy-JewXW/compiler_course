import error.ErrorVisitor;
import frontend.Lexer;
import frontend.Token;
import frontend.Parser;
import llvm.IRModule;
import llvm.IRVisitor;
import syntax.CompUnit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String outputFile = "output.txt";
    private static final String errorFile = "error.txt";
    private static final String llvmFile = "llvm_ir.txt";

    public static void main(String[] args) throws Exception {
        // 词法分析
        Lexer lexer = new Lexer(new BufferedReader(new FileReader(inputFile)));
        lexer.tokenize();
        ArrayList<Token> tokens = lexer.getTokens();

        // 语法分析
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parse();
        compUnit.output(outputFile);

        // 错误处理
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        compUnit.check();
        errorVisitor.print(errorFile);

        // 中间代码生成
        IRVisitor irVisitor = new IRVisitor(compUnit);
        irVisitor.visitCompUnit();
        IRModule irModule = IRModule.getInstance();
        irModule.toLLVM(llvmFile);
    }
}
