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
    private static HashMap<Function, Integer> localVarCnts = new HashMap<>();

    public static void setCurrentFunction(Function function) {
        localVarCnts.put(function, 0);
    }

    public static String getLocalVarName(Function function) {
        int p = localVarCnts.getOrDefault(function, 0);
        localVarCnts.put(function, p + 1);
        return "%v" + p;
    }

}
