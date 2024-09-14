package tools;

import frontend.Token;
import frontend.syntax.CompUnit;

import java.io.FileNotFoundException;
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
}
