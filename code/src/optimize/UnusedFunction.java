package optimize;

import middle.Module;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.instructions.CallInst;
import middle.component.instructions.Instruction;
import middle.component.types.PointerType;

import java.util.HashSet;
import java.util.Stack;

/* 函数分析类，删除无用的函数
 * 删除具有以下特征的函数，称其为无副作用函数：
 * 1. 函数体内没有调用其他函数
 * 2. 函数形参不含int []和char []
 * 3. 不使用全局变量
 */
public class UnusedFunction {
    public static void build(Module module) {
        detect(module);
        removeUnusedFunctions(module);
    }

    private static void detect(Module module) {
        for (Function function : module.getFunctions()) {
            if (sideEffect(function)) {
                function.setHasSideEffect(true);
            }
        }
    }

    private static boolean sideEffect(Function function) {
        if (function.getArguments()
                .stream().anyMatch(arg -> arg.getValueType() instanceof PointerType)) {
            return true;
        }

        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction instr : bb.getInstructions()) {
                if (instr instanceof CallInst
                        || instr.getOperands().stream()
                        .anyMatch(value -> value instanceof GlobalVar)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeUnusedFunctions(Module module) {
        Function main = module.getFunctions().get(module.getFunctions().size() - 1);
        HashSet<Function> called = new HashSet<>();
        Stack<Function> visited = new Stack<>();
        visited.push(main);
        while (!visited.isEmpty()) {
            Function x = visited.pop();
            if (x.isBuiltIn() || !called.add(x)) {
                continue;
            }
            for (BasicBlock bb : x.getBasicBlocks()) {
                for (Instruction instr : bb.getInstructions()) {
                    if (instr instanceof CallInst callInst) {
                        visited.push(callInst.getFunction());
                    }
                }
            }
        }
        module.getFunctions().removeIf(function -> !called.contains(function));
    }
}
