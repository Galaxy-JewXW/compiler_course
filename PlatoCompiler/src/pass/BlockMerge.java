package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BlockMerge {
    public static void run(Module module) {
        module.getFunctions().forEach(BlockMerge::mergeBlocks);
    }

    private static void mergeBlocks(Function function) {
        ArrayList<BasicBlock> blocks = function.getBasicBlocks();
        blocks.forEach(BlockMerge::tryMergeBlock);
        function.setBasicBlocks(blocks.stream()
                .filter(block -> !block.isDeleted())
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private static void tryMergeBlock(BasicBlock block) {
        if (block.isDeleted() || block.getNextBlocks().size() != 1) {
            return;
        }
        BasicBlock child = block.getNextBlocks().get(0);
        if (child.getPrevBlocks().size() != 1) {
            return;
        }
        mergeBlockWithChild(block, child);
    }

    private static void mergeBlockWithChild(BasicBlock parent, BasicBlock child) {
        // Remove last instruction from parent
        Instruction last = parent.getLastInstruction();
        last.deleteUse();
        parent.getInstructions().remove(last);

        // Transfer instructions from child to parent
        child.getInstructions().stream()
                .peek(inst -> {
                    if (inst instanceof PhiInst phiInst) {
                        handlePhiInstruction(phiInst, parent);
                    } else {
                        transferInstruction(inst, parent);
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));

        child.getInstructions().clear();

        // Finalize the merge
        child.replaceByNewValue(parent);
        child.setDeleted(true);
    }

    private static void handlePhiInstruction(PhiInst phiInst, BasicBlock parent) {
        ArrayList<BasicBlock> blocks = phiInst.getBlocks();
        ArrayList<Value> operands = phiInst.getOperands();
        phiInst.replaceByNewValue(operands.get(blocks.indexOf(parent)));
        phiInst.deleteUse();
    }

    private static void transferInstruction(Instruction inst, BasicBlock parent) {
        parent.addInstruction(inst);
        inst.setBasicBlock(parent);
    }
}