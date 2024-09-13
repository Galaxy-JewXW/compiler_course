package pass.Analysis;

import IR.IRModule;
import IR.types.PointerType;
import IR.values.*;
import IR.values.instructions.CallInst;
import IR.values.instructions.Instruction;

import java.util.HashSet;
import java.util.Stack;

public class FuncAnalysis {
    private final IRModule irModule;
    private final HashSet<Function> called = new HashSet<>();

    public FuncAnalysis(IRModule irModule) {
        this.irModule = irModule;
    }
    /*
    旨在分析函数是否有副作用,同时把无用的函数删了
    1.调用其他函数
    2.访问全局变量
    3.参数有指针类型
    */

    public void run() {
        for (Function function : irModule.getFunctions()) {
            for (Argument argument : function.getArguments()) {
                if (argument.getType() instanceof PointerType) {
                    function.setHasSideEffect();
                    break;
                }
            }
            for (BasicBlock bb : function.getBasicBlocks()) {
                for (Instruction instr : bb.getInstructions()) {
                    if (instr instanceof CallInst) {
                        function.setHasSideEffect();
                        continue;
                    }
                    for (Value value : instr.getOperands()) {
                        if (value instanceof GlobalVar) {
                            function.setHasSideEffect();
                            break;
                        }
                    }
                }
            }
        }
        Function main = irModule.getFunctions().get(
                irModule.getFunctions().size() - 1);
        Stack<Function> stack = new Stack<>();
        stack.push(main);
        while (!stack.isEmpty()) {
            Function x = stack.pop();
            if (x.isLibrary() || called.contains(x)) {
                continue;
            }
            called.add(x);
            for (BasicBlock bb : x.getBasicBlocks()) {
                for (Instruction instr : bb.getInstructions()) {
                    if (instr instanceof CallInst callInst) {
                        stack.push(callInst.getFunction());
                    }
                }
            }
        }
        irModule.getFunctions().removeIf(function -> !called.contains(function));
    }
}
