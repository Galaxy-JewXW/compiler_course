package optimize;

import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.io.GetcharInst;
import middle.component.instruction.io.GetintInst;
import middle.component.instruction.io.IOInst;

import java.util.HashSet;
import java.util.Stack;

// Code Removal
public class CodeRemoval {
    public static void run(Module module) {
        FunctionSideEffect.run(module);
        rmDeadCode(module);
        rmUnusedCode(module);
    }

    private static void rmDeadCode(Module module) {
        module.getFunctions().forEach(function ->
                function.getBasicBlocks().forEach(block -> {
                    // 使用 stream() 和 removeIf() 过滤无用指令
                    block.getInstructions().removeIf(CodeRemoval::isDeadCode);
                })
        );
    }

    // 判断指令是否是死代码
    private static boolean isDeadCode(Instruction instruction) {
        if (instruction.getUserList().isEmpty() && !instruction.getName().isEmpty()) {
            // 非副作用的指令应被移除
            if (!(instruction instanceof CallInst
                    || instruction instanceof GetintInst || instruction instanceof GetcharInst)) {
                instruction.removeOperands();
                return true;
            } else if (instruction instanceof CallInst callInst) {
                // 没有副作用的函数调用也应被移除
                if (instruction.getUserList().isEmpty()
                        && !callInst.getCalledFunction().hasSideEffects()) {
                    instruction.removeOperands();
                    return true;
                }
            }
        }
        return false;
    }

    private static void rmUnusedCode(Module module) {
        module.getFunctions().forEach(CodeRemoval::delete);
    }

    private static void delete(Function function) {
        HashSet<Instruction> usefulInstructions = findUseful(function);
        removeUseless(function, usefulInstructions);
    }

    // 查找有用的指令
    private static HashSet<Instruction> findUseful(Function function) {
        HashSet<Instruction> useful = new HashSet<>();
        function.getBasicBlocks().forEach(block ->
                block.getInstructions().stream()
                        .filter(CodeRemoval::isUseful)
                        .forEach(instruction -> getClosure(instruction, useful))
        );
        return useful;
    }

    // 获取所有依赖关系的闭包
    private static void getClosure(Instruction instruction, HashSet<Instruction> useful) {
        Stack<Instruction> stack = new Stack<>();
        stack.push(instruction);
        while (!stack.isEmpty()) {
            Instruction curInstruction = stack.pop();
            if (useful.add(curInstruction)) {
                curInstruction.getOperands().stream()
                        .filter(operand -> operand instanceof Instruction)
                        .map(operand -> (Instruction) operand)
                        .forEach(stack::push);
            }
        }
    }

    // 判断指令是否有用
    private static boolean isUseful(Instruction instruction) {
        return instruction.getName().isEmpty() || instruction instanceof IOInst ||
                (instruction instanceof CallInst callInst && callInst.getCalledFunction().hasSideEffects());
    }

    // 移除无用的指令
    private static void removeUseless(Function function, HashSet<Instruction> useful) {
        function.getBasicBlocks().forEach(block ->
                block.getInstructions().removeIf(instruction -> {
                    if (!useful.contains(instruction)) {
                        instruction.removeOperands();
                        return true;
                    }
                    return false;
                })
        );
    }
}
