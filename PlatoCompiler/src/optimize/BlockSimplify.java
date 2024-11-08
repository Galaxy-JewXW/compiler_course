package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.instruction.RetInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class BlockSimplify {
    public static void run(Module module) {
        Mem2Reg.run(module, false);
        for (int i = 0; i < 10; i++) {
            module.getFunctions().forEach(BlockSimplify::rearrange);
            Mem2Reg.run(module, false);
            module.getFunctions().forEach(BlockSimplify::merge);
            Mem2Reg.run(module, false);
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
    private static void merge(Function function) {
        for (BasicBlock block : function.getBasicBlocks()) {
            if (!block.isDeleted()) {
                if (block.getNextBlocks().size() == 1) {
                    BasicBlock child = block.getNextBlocks().get(0);
                    if (child.getPrevBlocks().size() == 1) {
                        Instruction jumpInstr = block.getLastInstruction();
                        jumpInstr.removeOperands();
                        block.getInstructions().remove(jumpInstr);
                        Iterator<Instruction> it = child.getInstructions().iterator();
                        while (it.hasNext()) {
                            Instruction instr = it.next();
                            if (instr instanceof PhiInst phiInst) {
                                ArrayList<BasicBlock> blocks = phiInst.getBlocks();
                                ArrayList<Value> operands = phiInst.getOperands();
                                phiInst.replaceByNewValue(operands.get(blocks.indexOf(block)));
                                phiInst.removeOperands();
                            } else {
                                block.getInstructions().add(instr);
                                instr.setBasicBlock(block);
                            }
                            it.remove();
                        }
                        child.replaceByNewValue(block);
                        child.setDeleted(true);
                    }
                }
            }
        }
        function.getBasicBlocks().removeIf(BasicBlock::isDeleted);
    }


}
