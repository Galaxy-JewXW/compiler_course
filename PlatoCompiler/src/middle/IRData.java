package middle;

import middle.component.BasicBlock;
import middle.component.ForLoop;
import middle.component.Function;
import middle.component.Module;

import java.util.HashMap;
import java.util.Stack;

public class IRData {
    // 计数器
    private static int globalVarCnt = 0;
    private static int constStringCnt = 0;
    private static int basicBlockCnt = 0;
    private static int paramCnt = 0;
    private static HashMap<Function, Integer> localVarCnts = new HashMap<>();

    private static Module currentModule = null;
    private static Function currentFunction = null;
    private static BasicBlock currentBasicBlock = null;

    private static Stack<ForLoop> forLoopStack = new Stack<>();
}
