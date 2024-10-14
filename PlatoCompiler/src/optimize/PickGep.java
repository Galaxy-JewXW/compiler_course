package optimize;

import middle.component.Module;
import middle.component.*;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;

import java.util.ArrayList;

// 将编译期能确定的计算地址指令外提
// 目前仅实现全局数组
public class PickGep {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            optimize(function);
        }
    }

    private static void optimize(Function function) {
        BasicBlock entry = function.getEntryBlock();
        for (int i = 1; i < function.getBasicBlocks().size(); i++) {
            BasicBlock block = function.getBasicBlocks().get(i);
            ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
            for (Instruction instruction : instructions) {
                if (instruction instanceof GepInst gepInst) {
                    if (gepInst.getPointer() instanceof GlobalVar
                            && gepInst.getIndex() instanceof ConstInt) {
                        gepInst.getBasicBlock().getInstructions().remove(gepInst);
                        gepInst.setBasicBlock(entry);
                        entry.getInstructions().add(0, gepInst);
                    }
                }
            }
        }
    }
}
