package optimize;

import middle.IRData;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Call;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.instruction.io.IOInst;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InlineFunc {
    private static HashMap<Function, HashSet<Function>> callMap;
    private static HashMap<Function, HashSet<Function>> calledByMap;

    public static void run(Module module) {
        boolean changed = true;
        while (changed) {
            changed = false;
            callMap = new HashMap<>();
            calledByMap = new HashMap<>();
            module.getFunctions().forEach(function -> {
                callMap.put(function, new HashSet<>());
                calledByMap.put(function, new HashSet<>());
            });
            // 初始化调用关系
            for (Function function : module.getFunctions()) {
                for (BasicBlock block : function.getBasicBlocks()) {
                    for (Instruction instruction : block.getInstructions()) {
                        if (instruction instanceof Call) {
                            if (instruction instanceof IOInst) {
                                continue;
                            }
                            CallInst callInst = (CallInst) instruction;
                            Function calledFunction = callInst.getCalledFunction();
                            callMap.get(function).add(calledFunction);
                            calledByMap.get(calledFunction).add(function);
                        }
                    }
                }
            }

            for (Function calledFunction : module.getFunctions()) {
                // 没有被任何一个函数调用
                if (calledByMap.get(calledFunction).isEmpty()) {
                    continue;
                }
                // 调用了其他函数，或为主函数
                if (!callMap.get(calledFunction).isEmpty()
                        || calledFunction.getName().equals("@main")) {
                    continue;
                }
                // 含有递归调用
                if (callMap.get(calledFunction).contains(calledFunction)) {
                    continue;
                }
                funcInline(calledFunction);
                changed = true;
            }
        }
    }

    public static void funcInline(Function function) {
        ArrayList<CallInst> calls = new ArrayList<>();
        for (Function caller : calledByMap.get(function)) {
            for (BasicBlock block : caller.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (instruction instanceof CallInst callInst && callInst.getCalledFunction().equals(function)) {
                        calls.add(callInst);
                    }
                }
            }
        }
        for (CallInst callInst : calls) {
            // 被调用者
            Function callee = callInst.getCalledFunction();
            // 调用者
            Function caller = callInst.getBasicBlock().getFunction();
            replace(callInst, caller, callee);
        }
    }

    public static void replace(CallInst callInst, Function caller, Function callee) {
        BasicBlock curBlock = callInst.getBasicBlock();
        ValueType returnType = caller.getReturnType();
        BasicBlock nextBlock = new BasicBlock(IRData.getBlockName());
        caller.getBasicBlocks().add(caller.getBasicBlocks().indexOf(curBlock) + 1, nextBlock);
        boolean isAfterCall = false;
        ArrayList<Instruction> instructions = new ArrayList<>(curBlock.getInstructions());
        for (Instruction instruction : instructions) {
            if (!isAfterCall && instruction instanceof CallInst) {
                isAfterCall = true;
                continue;
            }
            if (isAfterCall) {
                curBlock.getInstructions().remove(instruction);
                nextBlock.getInstructions().add(instruction);
                instruction.setBasicBlock(nextBlock);
            }
        }
        for (BasicBlock child : curBlock.getNextBlocks()) {
            for (Instruction instruction : child.getInstructions()) {
                if (instruction instanceof PhiInst phiInst
                        && phiInst.getBlocks().contains(curBlock)) {
                    phiInst.getBlocks().set(
                            phiInst.getBlocks().indexOf(curBlock), nextBlock);
                    nextBlock.addUse(phiInst);
                    curBlock.getUseList().removeIf(use -> use.getUser().equals(phiInst));
                }
            }
        }
        nextBlock.setNextBlocks(curBlock.getNextBlocks());
        for (BasicBlock child : curBlock.getNextBlocks()) {
            child.getPrevBlocks().set(
                    child.getPrevBlocks().indexOf(curBlock), nextBlock);
        }
        curBlock.setNextBlocks(new ArrayList<>());
        // 解决浅拷贝沈拷贝的问题
        Function copied = FunctionCopy.build(caller, callee);
    }
}
