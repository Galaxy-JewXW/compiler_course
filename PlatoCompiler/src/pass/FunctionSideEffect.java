package pass;

import middle.component.FuncParam;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.StoreInst;
import middle.component.instruction.io.IOInst;
import middle.component.model.Value;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionSideEffect {

    public static void run(Module module) {
        // 初始化 callMap
        Map<Function, Set<Function>> callMap = module.getFunctions().stream()
                .collect(Collectors.toMap(
                        function -> function,
                        FunctionSideEffect::getCalledFunctions
                ));

        // 迭代更新每个函数的副作用，传播 sideEffects
        boolean changed;
        do {
            changed = module.getFunctions().stream().anyMatch(function -> {
                Set<Function> callFunctions = callMap.get(function);
                boolean updated = callFunctions.stream().anyMatch(Function::hasSideEffects)
                        && !function.hasSideEffects();
                if (updated) {
                    function.setHasSideEffects(true);
                }
                return updated;
            });
        } while (changed);
    }

    // 判断函数是否有副作用
    private static boolean hasSideEffects(Function function) {
        return function.getBasicBlocks().stream()
                .flatMap(block -> block.getInstructions().stream())
                .anyMatch(FunctionSideEffect::isSideEffectInstruction);
    }

    // 判断指令是否为副作用指令
    private static boolean isSideEffectInstruction(Instruction instruction) {
        if (instruction instanceof IOInst) {
            return true;
        } else if (instruction instanceof StoreInst storeInst) {
            Value pointer = storeInst.getPointer();
            return pointer instanceof GlobalVar ||
                    (pointer instanceof GepInst gepInst &&
                            (gepInst.getPointer() instanceof GlobalVar || gepInst.getPointer() instanceof FuncParam));
        }
        return false;
    }

    // 获取函数内所有调用的函数
    private static Set<Function> getCalledFunctions(Function function) {
        return function.getBasicBlocks().stream()
                .flatMap(block -> block.getInstructions().stream())
                .filter(instruction -> instruction instanceof CallInst)
                .map(instruction -> ((CallInst) instruction).getCalledFunction())
                .collect(Collectors.toSet());
    }
}
