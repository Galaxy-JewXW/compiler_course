package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.RetInst;

import java.util.Collections;

public class RearrangeBlock {
    public static void run(Module module) {
        for (int i = 0; i < 10; i++) {
            module.getFunctions().forEach(RearrangeBlock::rearrange);
        }
    }

    private static void rearrange(Function function) {
        for (int i = 1; i < function.getBasicBlocks().size(); i++) {
            BasicBlock block = function.getBasicBlocks().get(i);
            Instruction lastInst = block.getLastInstruction();
            if (lastInst instanceof RetInst) {
                continue;
            }
            BasicBlock target = null;
            if (lastInst instanceof BrInst brInst) {
                if (brInst.isConditional()) {
                    target = brInst.getFalseBlock();
                } else {
                    target = brInst.getTrueBlock();
                }
            }
            if (target == null) {
                continue;
            }
            int index = function.getBasicBlocks().indexOf(target);
            if (i < function.getBasicBlocks().size()
                    && i + 1 < function.getBasicBlocks().size()) {
                Collections.swap(function.getBasicBlocks(), i + 1, index);
            }
        }
    }
}
