package optimize;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.model.Value;

public class PickGep {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            optimize(function);
        }
    }

    private static void optimize(Function function) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (!(instruction instanceof GepInst gepInst)) {
                        continue;
                    }
                    if (!(gepInst.getIndex() instanceof ConstInt)) {
                        continue;
                    }
                    if (gepInst.getOperands().stream().noneMatch(op -> op instanceof GepInst)) {
                        continue;
                    }
                    for (Value operand : gepInst.getOperands()) {
                        if (operand instanceof GepInst gepInst1
                                && gepInst1.getIndex() instanceof ConstInt constInt
                                && constInt.getIntValue() == 0) {
                            GepInst newGep = new GepInst(gepInst1.getPointer(), gepInst.getIndex());
                            BasicBlock basicBlock = gepInst.getBasicBlock();
                            newGep.setBasicBlock(basicBlock);
                            basicBlock.getInstructions().set(
                                    basicBlock.getInstructions().indexOf(gepInst), newGep);
                            basicBlock.getInstructions().remove(gepInst);
                            gepInst.replaceByNewValue(newGep);
                            gepInst.removeOperands();
                            changed = true;
                            break;
                        }
                    }
                    if (changed) {
                        break;
                    }
                }
                if (changed) {
                    break;
                }
            }
        }
    }
}
