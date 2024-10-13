package optimize;

import middle.IRData;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.instruction.RetInst;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class InlinedFunction {
    private static Module module;
    private static HashMap<Function, HashSet<Function>> callGraph;
    private static HashMap<Function, HashSet<Function>> reverseCallGraph;

    public static void run(Module currentModule) {
        module = currentModule;
        boolean hasChanged;
        do {
            initializeCallGraphs();
            hasChanged = module.getFunctions().stream()
                    .filter(InlinedFunction::shouldInlineFunction)
                    .map(function -> {
                        inline(function);
                        return true;
                    })
                    .reduce(false, (a, b) -> true);
        } while (hasChanged);
    }

    private static void initializeCallGraphs() {
        callGraph = new HashMap<>();
        reverseCallGraph = new HashMap<>();

        module.getFunctions().forEach(func -> {
            callGraph.put(func, new HashSet<>());
            reverseCallGraph.put(func, new HashSet<>());
        });

        module.getFunctions().forEach(func ->
                func.getBasicBlocks().stream()
                        .flatMap(block -> block.getInstructions().stream())
                        .filter(instr -> instr instanceof CallInst)
                        .map(instr -> (CallInst) instr)
                        .forEach(callInst -> {
                            Function target = callInst.getCalledFunction();
                            callGraph.get(func).add(target);
                            reverseCallGraph.get(target).add(func);
                        })
        );
    }

    private static boolean shouldInlineFunction(Function function) {
        return !reverseCallGraph.get(function).isEmpty() &&
                !function.getName().equals("@main") &&
                callGraph.get(function).isEmpty() &&
                !callGraph.get(function).contains(function);
    }

    private static void inline(Function function) {
        ArrayList<CallInst> calls = reverseCallGraph.get(function).stream()
                .flatMap(caller -> caller.getBasicBlocks().stream())
                .flatMap(block -> block.getInstructions().stream())
                .filter(instr -> instr instanceof CallInst)
                .map(instr -> (CallInst) instr)
                .filter(callInst -> callInst.getOperands().get(0).getName().equals(function.getName()))
                .collect(Collectors.toCollection(ArrayList::new));

        calls.forEach(call -> replaceCall(call, call.getBasicBlock().getFunction(), function));
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
        BasicBlock nextBlock = new BasicBlock(IRData.getBlockName());
        nextBlock.setFunction(callerFunction);
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

        Function copiedFunction = FunctionCopy.copyFunction(calledFunction);

        for (int i = 0; i < copiedFunction.getFuncParams().size(); i++) {
            Value fParam = copiedFunction.getFuncParams().get(i);
            Value rParam = call.getParameters().get(i);
            fParam.replaceByNewValue(rParam);
        }

        BrInst brInst = new BrInst(copiedFunction.getEntryBlock());
        brInst.setBasicBlock(currentBlock);
        currentBlock.addInstruction(brInst);
        currentBlock.addNextBlock(copiedFunction.getEntryBlock());
        copiedFunction.getEntryBlock().addPrevBlock(currentBlock);

        ArrayList<RetInst> rets = new ArrayList<>();
        ArrayList<BasicBlock> blocks = new ArrayList<>();
        for (BasicBlock block : copiedFunction.getBasicBlocks()) {
            for (Instruction instr : block.getInstructions()) {
                if (instr instanceof RetInst retInst) {
                    rets.add(retInst);
                    blocks.add(block);
                }
            }
        }
        if (returnType.equals(IntegerType.VOID)) {
            for (RetInst retInst : rets) {
                BrInst brInst1 = new BrInst(nextBlock);
                ArrayList<BasicBlock> child = new ArrayList<>();
                brInst1.setBasicBlock(retInst.getBasicBlock());
                retInst.getBasicBlock().getInstructions().remove(retInst);
                retInst.removeOperands();
                retInst.getBasicBlock().getInstructions().add(brInst1);
                child.add(nextBlock);
                retInst.getBasicBlock().setNextBlocks(child);
                nextBlock.addPrevBlock(retInst.getBasicBlock());
            }
        } else {
            PhiInst phiInst = new PhiInst(returnType, nextBlock, blocks);
            nextBlock.getInstructions().add(0, phiInst);
            for (RetInst retInst : rets) {
                phiInst.addValue(retInst.getBasicBlock(), retInst.getReturnValue());
                ArrayList<BasicBlock> child = new ArrayList<>();
                child.add(nextBlock);
                retInst.getBasicBlock().setNextBlocks(child);
                nextBlock.addPrevBlock(retInst.getBasicBlock());
                BrInst brInst1 = new BrInst(nextBlock);
                brInst1.setBasicBlock(retInst.getBasicBlock());
                retInst.getBasicBlock().getInstructions().remove(retInst);
                retInst.removeOperands();
                retInst.getBasicBlock().getInstructions().add(brInst1);
            }
            call.replaceByNewValue(phiInst);
        }
        for (BasicBlock block : copiedFunction.getBasicBlocks()) {
            callerFunction.getBasicBlocks().add(callerFunction.getBasicBlocks().indexOf(nextBlock), block);
            block.setFunction(callerFunction);
        }
        call.removeOperands();
        currentBlock.getInstructions().remove(call);
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

}