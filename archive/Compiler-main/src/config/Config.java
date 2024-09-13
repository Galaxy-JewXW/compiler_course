package config;

import java.io.File;

public class Config {
    public static String inputPath = "testfile.txt";
    public static String outputPath = "output.txt";
    public static String errorPath = "error.txt";
    public static String originLLVM = "llvm_origin.txt";
    public static String ansLLVM = "llvm_ir.txt";
    public static String backLLVM = "llvm_back.txt";
    public static String outLLVM = "llvm_ir.txt";
    public static String mipsPath = "mips.txt";
    public static boolean parserMessage = false;
    public static boolean checkError = true;
    public static boolean genLLVM = true;
    public static boolean genMIPS = true;
    public static boolean optimize = false;
    public static boolean char2str = true;

    public static void setOutLLVM(String path) {
        outLLVM = path;
    }

    public static void init() {
        clearFile(outputPath);
        clearFile(errorPath);
        clearFile(originLLVM);
        clearFile(backLLVM);
        clearFile(ansLLVM);
        clearFile(mipsPath);
    }

    private static void clearFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
