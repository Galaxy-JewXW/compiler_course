package middle;

import middle.component.ForLoop;
import middle.component.Function;

import java.util.HashMap;
import java.util.Stack;

public class IRData {
    // 计数器
    private static int constStringCnt = 0;
    private static int basicBlockCnt = 0;
    private static int paramCnt = 0;
    private static final HashMap<Function, Integer> localVarCnts = new HashMap<>();
    private static final Stack<ForLoop> loops = new Stack<>();

    public static void setCurrentFunction(Function function) {
        localVarCnts.put(function, 0);
    }

    public static String getLocalVarName(Function function) {
        int p = localVarCnts.getOrDefault(function, 0);
        localVarCnts.put(function, p + 1);
        return "%v" + p;
    }

    public static String getBasicBlockName() {
        return "b" + basicBlockCnt++;
    }

    public static String getParamName() {
        return "%a" + paramCnt++;
    }

    public static void resetParamCnt() {
        paramCnt = 0;
    }

    public static void resetBasicBlockCnt() {
        basicBlockCnt = 0;
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
