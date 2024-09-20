package optimize;

import middle.Module;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.instructions.CallInst;
import middle.component.instructions.Instruction;
import middle.component.model.Value;

import java.util.HashSet;
import java.util.Stack;

public class DeadCode {
    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            delete(function);
        }
    }

    private static void delete(Function function) {
        HashSet<Instruction> useful = findUseful(function);
        removeUseless(function, useful);
    }

    private static HashSet<Instruction> findUseful(Function function) {
        HashSet<Instruction> useful = new HashSet<>();
        for (BasicBlock block : function.getBasicBlocks()) {
            for (Instruction instruction : block.getInstructions()) {
                if (isUseful(instruction)) {
                    getClosure(instruction, useful);
                }
            }
        }
        return useful;
    }

    private static void getClosure(Instruction instruction,
                                   HashSet<Instruction> useful) {
        Stack<Instruction> stack = new Stack<>();
        stack.push(instruction);
        while (!stack.isEmpty()) {
            Instruction curInstruction = stack.pop();
            if (useful.add(curInstruction)) {
                for (Value operand : curInstruction.getOperands()) {
                    if (operand instanceof Instruction instruction1) {
                        stack.push(instruction1);
                    }
                }
            }
        }
    }

    // br store ret 部分call是没有名字的
    private static boolean isUseful(Instruction instruction) {
        return instruction.getName().isEmpty() || instruction instanceof CallInst;
    }

    private static void removeUseless(Function function, HashSet<Instruction> useful) {
        for (BasicBlock block : function.getBasicBlocks()) {
            block.getInstructions().removeIf(instruction -> {
                if (!useful.contains(instruction)) {
                    // 如果指令不是有用的，删除其使用并将其移除
                    instruction.deleteUse();
                    return true;
                }
                return false;
            });
        }
    }
}
