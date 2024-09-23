package middle;

import middle.component.BasicBlock;
import middle.component.ForLoop;
import middle.component.Function;

import java.util.Stack;

public class IRData {
    // 计数器
    private static int constStringCnt = 0;
    private static Function currentFunction = null;
    private static BasicBlock currentBlock = null;
    private static int cnt = 0;
    private static final Stack<ForLoop> loops = new Stack<>();

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

    public static BasicBlock getCurrentBlock() {
        return currentBlock;
    }

    public static void setCurrentFunction(Function function) {
        currentFunction = function;
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

}
