package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;

import java.util.ArrayList;

public class SingleJumpRemoval {
    public static void build(Module module) {
        module.getFunctions().forEach(SingleJumpRemoval::buildFunction);
    }

    private static void buildFunction(Function function) {
        boolean changed;
        do {
            changed = false;
            ArrayList<BasicBlock> blocks = new ArrayList<>(function.getBasicBlocks());

            for (int i = 1; i < blocks.size(); ++i) {
                BasicBlock block = blocks.get(i);
                if (isTarget(block)) {
                    BrInst brInst = (BrInst) block.getLastInstruction();
                    BasicBlock target = brInst.getTrueBlock();
                    BasicBlock parent = block.getPrevBlocks().get(0);
                    assert target != null;
                    if (!target.equals(block)) {
                        parent.getNextBlocks().set(parent.getNextBlocks().indexOf(block), target);
                        target.getPrevBlocks().set(target.getPrevBlocks().indexOf(block), parent);
                        block.replaceByNewValue(target);
                        for (Instruction inst : block.getInstructions()) {
                            inst.deleteUse();
                        }
                        block.getInstructions().clear();
                        function.getBasicBlocks().remove(block);
                        for (Instruction inst : target.getInstructions()) {
                            if (!(inst instanceof PhiInst phiInst)) {
                                continue;
                            }
                            if (phiInst.getBlocks().contains(target)) {
                                phiInst.getBlocks().set(phiInst.getBlocks().indexOf(target), parent);
                            }
                        }
                        changed = true;
                    }
                }
            }
        } while (changed);

    }

    private static boolean isTarget(BasicBlock block) {
        if (block.getInstructions().size() == 1) {
            if (block.getLastInstruction() instanceof BrInst brInst) {
                return !brInst.isConditional()
                        && block.getPrevBlocks().size() == 1
                        && block.getPrevBlocks().get(0).getLastInstruction()
                        instanceof BrInst;
            }
        }
        return false;
    }
}