package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.io.GetcharInst;
import middle.component.instruction.io.GetintInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

// Code Removal
public class CodeRemoval {
    public static void run(Module module) {
        rmDeadCode(module);
        rmUsedCode(module);
    }

    private static void rmDeadCode(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction.getUseList().isEmpty() && !instruction.getName().isEmpty()
                            && !(instruction instanceof CallInst || instruction instanceof GetintInst
                            || instruction instanceof GetcharInst)) {
                        instruction.deleteUse();
                        block.getInstructions().remove(instruction);
                    }
                    if (instruction instanceof CallInst callInst) {
                        Function called = callInst.getCalledFunction();
                        if (instruction.getUseList().isEmpty()
                                && !called.hasSideEffects()) {
                            instruction.deleteUse();
                            block.getInstructions().remove(instruction);
                        }
                    }
                }
            }
        }
    }

    private static void rmUsedCode(Module module) {
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

    // br store ret 部分call output call 是没有副作用的
    private static boolean isUseful(Instruction instruction) {
        return instruction.getName().isEmpty()
                || (instruction instanceof CallInst callInst
                && callInst.getCalledFunction().hasSideEffects());
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
