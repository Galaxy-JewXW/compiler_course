import backend.MipsBuilder;
import backend.MipsFile;
import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.syntax.CompUnit;
import frontend.token.Token;
import middle.IRBuilder;
import middle.Visitor;
import middle.component.Module;
import optimize.Optimizer;
import tools.Printer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    private static final String inputFile = "testfile.txt";
    private static final String lexerOutput = "lexer.txt";
    private static final String parserOutput = "parser.txt";
    private static final String errorOutput = "error.txt";
    private static final String symbolOutput = "symbol.txt";
    private static final String llvmOutput = "llvm_ir.txt";
    private static final String irOutput = "ir.txt"; // 优化后的中间代码
    private static final String mipsOutput = "mips.txt";

    private static final boolean toLLVM = true;
    private static final boolean toMips = true;

    private static final boolean optimize = true;

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
        visitor.visit();
        System.out.println("visitor complete.");
        // 异常处理
        Printer.printErrors(ErrorHandler.getInstance().getErrors(), errorOutput);
        if (!ErrorHandler.getInstance().getErrors().isEmpty()) {
            return;
        }
        // 打印符号表
        Printer.printSymbols(symbolOutput);
        if (toLLVM) {        // 生成未优化中间代码
            IRBuilder irBuilder = new IRBuilder(compUnit);
            irBuilder.build();
            Printer.printIr(Module.getInstance(), llvmOutput);
            if (optimize) {
                // 中间代码优化
                Optimizer optimizer = new Optimizer(Module.getInstance());
                optimizer.optimize();
                Module.getInstance().updateId();
                Printer.printIr(Module.getInstance(), irOutput);
            }
            if (toMips) {// 目标代码生成
                MipsBuilder mipsBuilder = new MipsBuilder(Module.getInstance(), optimize);
                mipsBuilder.build(optimize);
                Printer.printMips(MipsFile.getInstance(), mipsOutput);
            }
        }
    }
}
