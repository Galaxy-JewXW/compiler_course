import IR.IRModule;
import IR.Visitor;
import backend.MIPSGenerator;
import config.Config;
import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import node.CompUnit;
import pass.Analysis.DelRedundantInst;
import pass.Analysis.RemovePhi;
import pass.PassModule;
import pass.RegAlloc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws IOException {
        Config.init();//清空输出文件等
        String in = Files.readString(Paths.get(Config.inputPath));//input
        Lexer lexer = new Lexer(in);
        Parser parser = new Parser(lexer);
        CompUnit compUnit = parser.compUnit();
        if (Config.parserMessage) {
            compUnit.print();
        }
        if (Config.checkError) {
            compUnit.checkError();
            ErrorHandler.getInstance().logErrors();
            if (ErrorHandler.getInstance().hasError()) {
                return;
            }
        }
        if (Config.genLLVM) {
            Visitor visitor = new Visitor();
            visitor.visitCompUnit(compUnit);
            Config.outLLVM = Config.optimize ? Config.originLLVM : Config.ansLLVM;
            IRModule.getInstance().genLLVm();
            if (Config.optimize) {
                PassModule.getInstance().run(IRModule.getInstance());
                Config.setOutLLVM(Config.ansLLVM);
                IRModule.getInstance().genLLVm();
            }
            if (Config.genMIPS) {
                new DelRedundantInst(IRModule.getInstance()).run();
                if (Config.optimize) {
                    new RegAlloc(IRModule.getInstance()).run();
                    new RemovePhi(IRModule.getInstance()).run();
                    Config.setOutLLVM(Config.backLLVM);
                    IRModule.getInstance().genLLVm();
                }
                MIPSGenerator.getInstance().genMIPS();
            }
        }
    }
}