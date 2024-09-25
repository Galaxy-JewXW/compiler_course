package optimize;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Use;
import middle.component.model.User;
import middle.component.model.Value;

import java.util.HashSet;

public class CertainBranch {
    private static HashSet<BasicBlock> deleteBuffer;
    private static Module module;

    public static void run(Module module) {
        CertainBranch.module = module;
        for (Function function : module.getFunctions()) {
            deleteBuffer = new HashSet<>();
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (instruction instanceof BrInst brInst) {
                        Value value = brInst.getOperands().get(0);
                        if (!(value instanceof ConstInt constInt)) {
                            continue;
                        }
                        BasicBlock trueBlock = brInst.getTrueBlock();
                        BasicBlock falseBlock = brInst.getFalseBlock();
                        BrInst newInst;
                        if (constInt.getIntValue() == 1) {
                            newInst = new BrInst(block, trueBlock);
                            optimizePhi(block, falseBlock);
                            block.getNextBlocks().remove(falseBlock);
                            falseBlock.getPrevBlocks().remove(block);
                        } else {
                            newInst = new BrInst(block, falseBlock);
                            optimizePhi(block, trueBlock);
                            block.getNextBlocks().remove(trueBlock);
                            trueBlock.getPrevBlocks().remove(block);
                        }
                        if (trueBlock.getPrevBlocks().isEmpty()) {
                            deleteBuffer.add(trueBlock);
                        }
                        if (falseBlock.getPrevBlocks().isEmpty()) {
                            deleteBuffer.add(falseBlock);
                        }
                        brInst.deleteUse();
                        block.getInstructions().set(
                                block.getInstructions().indexOf(brInst), newInst);
                    }
                }
            }
            for (BasicBlock block : deleteBuffer) {
                for (Instruction instruction : block.getInstructions()) {
                    instruction.deleteUse();
                }
            }
            function.getBasicBlocks().removeIf(
                    block -> deleteBuffer.contains(block));
        }
    }

    private static void optimizePhi(BasicBlock block1, BasicBlock block2) {
        for (Use use : block1.getUseList()) {
            User user = use.getUser();
            if (user instanceof PhiInst phiInst
                    && phiInst.getBasicBlock().equals(block2)) {
                for (int i = 0; i < phiInst.getBlocks().size() - 1; i++) {
                    if (phiInst.getBlocks().get(i).equals(block1)) {
                        phiInst.getBlocks().remove(i);
                        phiInst.getOperands().remove(i);
                        i--;
                    }
                }
            }
        }
    }
}
