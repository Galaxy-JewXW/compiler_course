package tools;

import backend.MipsFile;
import error.Error;
import frontend.TableManager;
import frontend.syntax.CompUnit;
import frontend.token.Token;
import middle.component.Module;

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

    public static void printSymbols(String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        TableManager.getInstance1().show();
        System.setOut(origin);
    }

    public static void printIr(Module module, String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        System.out.println(module);
        System.setOut(origin);
    }

    public static void printMips(MipsFile mipsFile, String path) throws FileNotFoundException {
        PrintStream origin = System.out;
        System.setOut(new PrintStream(path));
        System.out.println(mipsFile);
        System.setOut(origin);
    }
}
