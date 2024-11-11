package backend.utils;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.TruncInst;
import middle.component.instruction.ZextInst;

import java.util.ArrayList;

public class ZextRemoval {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof ZextInst zextInst) {
                        instruction.replaceByNewValue(zextInst.getOriginValue());
                        instruction.removeOperands();
                        block.getInstructions().remove(instruction);
                    }
                }
            }
        }
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof TruncInst truncInst) {
                        if (truncInst.getOriginValue().getValueType().equals(truncInst.getValueType())) {
                            instruction.replaceByNewValue(truncInst.getOriginValue());
                            instruction.removeOperands();
                            block.getInstructions().remove(instruction);
                        }
                    }
                }
            }
        }
    }
}
