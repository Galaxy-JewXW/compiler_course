package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.AllocInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.model.User;
import middle.component.type.ArrayType;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class UnusedLocalArray {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof AllocInst allocInst) {
                        if (isUnusedLocalArray(allocInst)) {
                            basicBlock.getInstructions().removeAll(allocInst.getGepInsts());
                            basicBlock.getInstructions().removeAll(allocInst.getStoreInsts());
                            basicBlock.getInstructions().remove(allocInst);
                        }
                    }
                }
            }
        }
    }

    private static boolean isUnusedLocalArray(AllocInst allocInst) {
        ValueType valueType = allocInst.getTargetType();
        if (!(valueType instanceof ArrayType)) {
            return false;
        }
        ArrayList<User> users = allocInst.getUserList();
        for (User user : users) {
            if (user instanceof GepInst gepInst) {
                if (!allocInst.getGepInsts().contains(gepInst)) {
                    return false;
                }
            }
        }
        return true;
    }
}
