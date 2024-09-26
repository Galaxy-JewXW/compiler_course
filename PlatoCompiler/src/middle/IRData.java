package middle;

import middle.component.BasicBlock;
import middle.component.ConstString;
import middle.component.ForLoop;
import middle.component.Function;

import java.util.HashMap;
import java.util.Stack;

public class IRData {
    private static final Stack<ForLoop> loops = new Stack<>();
    private static final HashMap<String, ConstString> constStrings = new HashMap<>();
    // 计数器
    private static int constStringCnt = 0;
    private static Function currentFunction = null;
    private static BasicBlock currentBlock = null;
    private static int cnt = 0;
    private static boolean insect = true;

    public static boolean isInsect() {
        return insect;
    }

    public static void setInsect(boolean insect) {
        IRData.insect = insect;
    }

    public static String getVarName() {
        return "%" + cnt++;
    }

    public static String getBlockName() {
        return Integer.toString(cnt++);
    }

    public static void reset() {
        cnt = 0;
    }

    public static Function getCurrentFunction() {
        return currentFunction;
    }

    public static void setCurrentFunction(Function function) {
        currentFunction = function;
    }

    public static BasicBlock getCurrentBlock() {
        return currentBlock;
    }

    public static void setCurrentBlock(BasicBlock currentBlock) {
        IRData.currentBlock = currentBlock;
    }

    public static String getConstStringName() {
        return "@.s." + constStringCnt++;
    }

    public static void push(ForLoop forLoop) {
        loops.push(forLoop);
    }

    public static ForLoop pop() {
        return loops.pop();
    }

    public static ForLoop peek() {
        return loops.peek();
    }

    public static boolean containsString(String string) {
        return constStrings.containsKey(string);
    }

    public static void putConstString(String string, ConstString constString) {
        constStrings.put(string, constString);
    }

    public static ConstString getConstString(String string) {
        return constStrings.get(string);
    }

}
