package middle;

import middle.component.BasicBlock;
import middle.component.ForLoop;
import middle.component.Function;
import middle.component.Module;

import java.util.HashMap;
import java.util.Stack;

public class IRData {
    // 计数器
    private static int constStringCnt = 0;
    private static int basicBlockCnt = 0;
    private static int paramCnt = 0;
    private static final HashMap<Function, Integer> localVarCnts = new HashMap<>();

    public static void setCurrentFunction(Function function) {
        localVarCnts.put(function, 0);
    }

    public static String getLocalVarName(Function function, boolean update) {
        int p = localVarCnts.getOrDefault(function, 0);
        if (!update) {
            return "%v" + p;
        }
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

}
