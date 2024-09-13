import frontend.Token;
import llvm.IRModule;
import llvm.IRVisitor;
import error.ErrorVisitor;
import frontend.Lexer;
import frontend.Parser;
import syntax.CompUnit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String outputFile = "output.txt";
    private static final String errorFile = "error.txt";
    private static final String llvmFile = "llvm_ir.txt";

    public static void main(String[] args) throws Exception {
        // 词法分析
        String inputString = Files.readString(Paths.get(inputFile));
        Lexer lexer = new Lexer(inputString);
        ArrayList<Token> tokens = lexer.tokenize();

        // 语法分析
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parse();
        compUnit.output(outputFile);

        // 错误处理
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        compUnit.check();
        errorVisitor.print(new BufferedWriter(new FileWriter(errorFile)));
        if (errorVisitor.hasError()) {
            throw new RuntimeException("Have errors in testfile.txt. Check errors.txt");
        }

        // 中间代码生成
        IRVisitor irVisitor = new IRVisitor(compUnit);
        irVisitor.visitCompUnit();
        IRModule irModule = IRModule.getInstance();
        irModule.toLLVM(llvmFile);

        // 目标代码生成
    }
}