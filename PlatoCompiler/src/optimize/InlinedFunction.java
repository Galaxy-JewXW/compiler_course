package optimize;

import middle.IRData;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.*;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InlinedFunction {
    private static Module module;
    private static HashMap<Function, HashSet<Function>> callGraph;
    private static HashMap<Function, HashSet<Function>> reverseCallGraph;

    /**
     * 对模块中的函数进行内联优化
     *
     * @param currentModule 当前的LLVM IR模块
     */
    public static void run(Module currentModule) {
        module = currentModule;
        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;
            initializeCallGraphs();
            for (Function calledFunction : module.getFunctions()) {
                if (shouldInlineFunction(calledFunction)) {
                    inline(calledFunction);
                    hasChanged = true;
                }
            }
        }
    }

    // 初始化调用图与反向调用图
    private static void initializeCallGraphs() {
        callGraph = new HashMap<>();
        reverseCallGraph = new HashMap<>();
        for (Function func : module.getFunctions()) {
            callGraph.put(func, new HashSet<>());
            reverseCallGraph.put(func, new HashSet<>());
        }
        for (Function func : module.getFunctions()) {
            for (BasicBlock block : func.getBasicBlocks()) {
                for (Instruction instr : block.getInstructions()) {
                    if (instr instanceof CallInst callInst) {
                        Function target = (Function) callInst.getOperands().get(0);
                        callGraph.get(func).add(target);
                        reverseCallGraph.get(target).add(func);
                    }
                }
            }
        }
    }

    // 是否可以进行内联
    private static boolean shouldInlineFunction(Function function) {
        return !reverseCallGraph.get(function).isEmpty() &&
                !function.getName().equals("@main") &&
                callGraph.get(function).isEmpty() &&
                !callGraph.get(function).contains(function);
    }

    private static void inline(Function function) {
        ArrayList<CallInst> callsToInline = findCallsToInline(function);
        for (CallInst call : callsToInline) {
            Function callerFunction = call.getBasicBlock().getFunction();
            replaceCall(call, callerFunction, function);
        }
    }

    /**
     * 查找需要内联的调用指令
     *
     * @param function 被调用的函数
     * @return 需要内联的调用指令列表
     */
    private static ArrayList<CallInst> findCallsToInline(Function function) {
        ArrayList<CallInst> calls = new ArrayList<>();
        for (Function caller : reverseCallGraph.get(function)) {
            for (BasicBlock block : caller.getBasicBlocks()) {
                for (Instruction instr : block.getInstructions()) {
                    if (instr instanceof CallInst callInst
                            && callInst.getOperands().get(0).getName()
                            .equals(function.getName())) {
                        calls.add(callInst);
                    }
                }
            }
        }
        return calls;
    }

    /**
     * 替换调用指令为内联的函数体
     *
     * @param call           调用指令
     * @param callerFunction 调用者函数
     * @param calledFunction 被调用的函数
     */
    private static void replaceCall(CallInst call, Function callerFunction,
                                    Function calledFunction) {
        BasicBlock currentBlock = call.getBasicBlock();
        ValueType returnType = calledFunction.getReturnType();
        BasicBlock nextBlock = new BasicBlock(IRData.getBlockName(), callerFunction);
        callerFunction.getBasicBlocks().add(
                callerFunction.getBasicBlocks().indexOf(currentBlock) + 1, nextBlock);
        moveInstructionsAfterCall(currentBlock, nextBlock, call);
        updatePhi(currentBlock, nextBlock);
        // 更新前后驱关系
        nextBlock.setNextBlocks(currentBlock.getNextBlocks());
        for (BasicBlock child : currentBlock.getNextBlocks()) {
            child.getPrevBlocks().set(child.getPrevBlocks().indexOf(currentBlock), nextBlock);
        }
        currentBlock.setNextBlocks(new ArrayList<>());

        Function copiedFunction = FunctionCopy.copyFunction(calledFunction, callerFunction);
        replaceFParams(copiedFunction, call);
        linkInlinedFunction(currentBlock, copiedFunction);
        handleReturnInstructions(copiedFunction, nextBlock, returnType, call);
        integrateInlinedBlocks(callerFunction, copiedFunction, nextBlock);
        removeCallInstruction(call, currentBlock);
    }

    private static void moveInstructionsAfterCall(BasicBlock currentBlock,
                                                  BasicBlock nextBlock, CallInst call) {
        boolean reachAfterCall = false;
        ArrayList<Instruction> instructionsCopy = new ArrayList<>(
                currentBlock.getInstructions());
        for (Instruction instr : instructionsCopy) {
            if (!reachAfterCall && instr.equals(call)) {
                reachAfterCall = true;
                continue;
            }
            if (reachAfterCall) {
                currentBlock.getInstructions().remove(instr);
                nextBlock.getInstructions().add(instr);
                instr.setBasicBlock(nextBlock);
            }
        }
    }

    // 出现新block之后，phi指令的标记可能会发生变化
    private static void updatePhi(BasicBlock currentBlock, BasicBlock nextBlock) {
        for (BasicBlock child : currentBlock.getNextBlocks()) {
            for (Instruction instr : child.getInstructions()) {
                if (instr instanceof PhiInst phiInst) {
                    if (phiInst.getBlocks().contains(currentBlock)) {
                        phiInst.getBlocks().set(phiInst.getBlocks().
                                indexOf(currentBlock), nextBlock);
                        nextBlock.addUse(instr);
                        currentBlock.deleteUser(instr);
                    }
                }
            }
        }
    }

    private static void replaceFParams(Function copiedFunction, CallInst call) {
        for (int i = 0; i < copiedFunction.getFuncParams().size(); i++) {
            Value fParam = copiedFunction.getFuncParams().get(i);
            Value rParam = call.getOperands().get(i + 1);
            fParam.replaceByNewValue(rParam);
        }
    }

    private static void linkInlinedFunction(BasicBlock currentBlock, Function copiedFunction) {
        BrInst brInst = new BrInst(currentBlock, copiedFunction.getBasicBlocks().get(0));
        currentBlock.addInstruction(brInst);
        currentBlock.addNextBlock(copiedFunction.getBasicBlocks().get(0));
        copiedFunction.getBasicBlocks().get(0).addPrevBlock(currentBlock);
    }

    private static void handleReturnInstructions(Function copiedFunction, BasicBlock nextBlock,
                                                 ValueType returnType, CallInst call) {
        ArrayList<RetInst> returnInstructions = new ArrayList<>();
        for (BasicBlock block : copiedFunction.getBasicBlocks()) {
            for (Instruction instr : block.getInstructions()) {
                if (instr instanceof RetInst retInst) {
                    returnInstructions.add(retInst);
                }
            }
        }
        if (!returnType.equals(IntegerType.VOID)) {
            // phi指令不会自动添加到基本块中
            PhiInst phi = new PhiInst(returnType);
            nextBlock.getInstructions().add(0, phi);
            phi.setBasicBlock(nextBlock);
            for (RetInst ret : returnInstructions) {
                phi.addValue(ret.getBasicBlock(), ret.getOperands().get(0));
                updateBlockForReturn(ret.getBasicBlock(), nextBlock);
                replaceRetToBr(ret, nextBlock);
            }
            call.replaceByNewValue(phi);
        } else {
            for (RetInst ret : returnInstructions) {
                updateBlockForReturn(ret.getBasicBlock(), nextBlock);
                replaceRetToBr(ret, nextBlock);
            }
        }
    }

    private static void updateBlockForReturn(BasicBlock returnBlock, BasicBlock nextBlock) {
        ArrayList<BasicBlock> child = new ArrayList<>();
        child.add(nextBlock);
        returnBlock.setNextBlocks(child);
        nextBlock.addPrevBlock(returnBlock);
    }

    private static void replaceRetToBr(RetInst ret, BasicBlock nextBlock) {
        BrInst brInst = new BrInst(ret.getBasicBlock(), nextBlock);
        ret.getBasicBlock().getInstructions().remove(ret);
        ret.deleteUse();
        ret.getBasicBlock().addInstruction(brInst);
    }

    private static void integrateInlinedBlocks(Function callerFunction,
                                               Function copiedFunction, BasicBlock nextBlock) {
        for (BasicBlock block : copiedFunction.getBasicBlocks()) {
            callerFunction.getBasicBlocks().add(
                    callerFunction.getBasicBlocks().indexOf(nextBlock), block);
            block.setFunction(callerFunction);
        }
    }

    private static void removeCallInstruction(CallInst call, BasicBlock currentBlock) {
        call.deleteUse();
        currentBlock.getInstructions().remove(call);
    }
}
