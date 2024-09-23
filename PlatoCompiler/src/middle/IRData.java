package middle;

import middle.component.Function;

import java.util.HashMap;

public class IRData {
    // 计数器
    private static int constStringCnt = 0;
    private static int basicBlockCnt = 0;
    private static int paramCnt = 0;
    private static final HashMap<Function, Integer> localVarCnts = new HashMap<>();

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

}
