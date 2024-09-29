package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockMerge {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                if (!block.isDeleted() && block.getNextBlocks().size() == 1) {
                    BasicBlock child = block.getNextBlocks().get(0);
                    if (child.getPrevBlocks().size() == 1) {
                        Instruction last = block.getLastInstruction();
                        last.deleteUse();
                        block.getInstructions().remove(last);
                        Iterator<Instruction> it = child.getInstructions().iterator();
                        while (it.hasNext()) {
                            Instruction inst = it.next();
                            if (inst instanceof PhiInst phiInst) {
                                ArrayList<BasicBlock> blocks = phiInst.getBlocks();
                                ArrayList<Value> operands = phiInst.getOperands();
                                phiInst.replaceByNewValue(
                                        operands.get(blocks.indexOf(block)));
                                phiInst.deleteUse();
                            } else {
                                block.addInstruction(inst);
                                inst.setBasicBlock(block);
                            }
                            it.remove();
                        }
                        child.replaceByNewValue(block);
                        child.setDeleted(true);
                    }
                }
            }
            function.getBasicBlocks().removeIf(BasicBlock::isDeleted);
        }
    }
}
