package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.RetInst;

import java.util.Collections;

public class BlockSimplify {
    public static void run(Module module) {
        Mem2Reg.run(module, false);
        for (int i = 0; i < 10; i++) {
            module.getFunctions().forEach(BlockSimplify::rearrange);
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

    // TODO: 基本块合并


}
