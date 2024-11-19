package optimize;

import middle.IRData;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;

import java.util.ArrayList;

public class DivideCall {
    private static Module module;

    public static void run(Module module) {
        Mem2Reg.run(module, false);
        DivideCall.module = module;
        for (Function function : module.getFunctions()) {
            divide(function);
        }
    }

    private static void divide(Function function) {
        ArrayList<CallInst> callInsts = new ArrayList<>();
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            for (Instruction instruction : basicBlock.getInstructions()) {
                if (instruction instanceof CallInst callInst) {
                    callInsts.add(callInst);
                }
            }
        }
        for (CallInst callInst : callInsts) {
            replaceCall(callInst, callInst.getBasicBlock().getFunction());
        }
    }

    private static void replaceCall(CallInst call, Function callerFunction) {
        BasicBlock currentBlock = call.getBasicBlock();
        BasicBlock nextBlock = new BasicBlock(IRData.getBlockName());
        nextBlock.setFunction(callerFunction);
        callerFunction.getBasicBlocks().add(
                callerFunction.getBasicBlocks().indexOf(currentBlock) + 1, nextBlock);
        moveInstructionsAfterCall(currentBlock, nextBlock, call);
        currentBlock.getInstructions().add(new BrInst(nextBlock));
        updatePhi(currentBlock, nextBlock);
        Mem2Reg.run(module, false);
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
