package optimize;

import middle.component.BasicBlock;
import middle.component.FuncParam;
import middle.component.Function;
import middle.component.model.Value;

import java.util.HashMap;

public class FunctionCopy {
    // 记录原值和复制值之间的映射关系
    private static HashMap<Value, Value> map = new HashMap<>();
    private static Function callFunc;

    public static Function build(Function caller, Function callee) {
        Function answerFunction = new Function(callee.getName() + "_kobe",
                callee.getReturnType(), false);
        for (FuncParam funcParam : callee.getFuncParams()) {
            FuncParam funcParam1 = new FuncParam(callee.getName(), callee.getValueType());
            map.put(funcParam, funcParam1);
            answerFunction.addFuncParam(funcParam1);
        }
        for (BasicBlock block : callee.getBasicBlocks()) {
            BasicBlock block1 = new BasicBlock(block.getName(), answerFunction);
            answerFunction.addBasicBlock(block1);
            map.put(block, block1);
        }
        return answerFunction;
    }
}
