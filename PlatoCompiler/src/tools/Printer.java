package tools;

import error.Error;
import frontend.syntax.CompUnit;
import frontend.token.Token;

import java.io.*;
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
}
