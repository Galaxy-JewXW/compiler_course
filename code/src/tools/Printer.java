package tools;

import error.Error;
import frontend.syntax.CompUnit;
import frontend.token.Token;
import middle.Function;
import middle.GlobalVar;
import middle.Module;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

// 工具类，负责中间结构的输出
public class Printer {
    public static void printTokens(ArrayList<Token> tokens, String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.setOut(origin);
    }

    public static void printCompUnit(CompUnit compUnit, String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        compUnit.print();
        System.setOut(origin);
    }

    public static void printErrors(ArrayList<Error> errors, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        if (!errors.isEmpty()) {
            System.out.println("Got " + errors.size() + " errors.");
        } else {
            System.out.println("No errors found.");
        }
        writer.flush();
        for (Error error : errors) {
            System.out.println(error.showMessage());
            writer.write(error.toString());
            writer.newLine();
        }
        writer.close();
    }

    public static void printLLVM(Module module, String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        String builtInFuncs = """
                declare i32 @getint()
                declare i32 @getchar()
                declare void @putint(i32)
                declare void @putch(i8)
                declare void @putstr(i8*)""";
        System.out.println(builtInFuncs);
        for (GlobalVar globalVar : module.getGlobalVars()) {
            System.out.println(globalVar);
        }
        System.out.print("\n");
        for (Function function : module.getFunctions()) {
            function.toLLVM();
            System.out.print("\n");
        }
        System.setOut(origin);
    }
}
