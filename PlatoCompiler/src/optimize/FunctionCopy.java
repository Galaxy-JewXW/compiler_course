package optimize;

import middle.component.BasicBlock;
import middle.component.FuncParam;
import middle.component.Function;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class FunctionCopy {
    // 记录原值和复制值之间的映射关系
    private static final HashMap<Value, Value> map = new HashMap<>();
    private static final HashSet<BasicBlock> visited = new HashSet<>();
    private static final LinkedHashSet<Instruction> copiedInstructions = new LinkedHashSet<>();
    private static final HashSet<PhiInst> phiInsts = new HashSet<>();
    private static Function callFunc;

    public static Function build(Function caller, Function callee) {
        // man!
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
        copyBlock(callee.getBasicBlocks().get(0));
        return answerFunction;
    }

    private static void copyBlock(BasicBlock block) {
        visited.add(block);
        for (Instruction instruction : block.getInstructions()) {
            copyInstruction(instruction, block, false);
        }
        for (BasicBlock next : block.getNextBlocks()) {
            if (!visited.contains(next)) {
                copyBlock(next);
            }
        }
    }

    private static void copyInstruction(Instruction instruction, BasicBlock block, boolean flag) {
        Instruction copied = null;
    }
}
