package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.ArrayList;

public class OptimizePhi {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {

                ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (!(instruction instanceof PhiInst phiInst)) {
                        break;
                    }
                    ArrayList<Value> operands = phiInst.getOperands();
                    for (int i = 0; i < operands.size(); i++) {
                        if (phiInst.getBlocks().get(i).isDeleted()) {
                            operands.remove(i);
                            phiInst.getBlocks().remove(i);
                            i--;
                        }
                    }
                    boolean flag = true;
                    for (Value operand : operands) {
                        if (!operand.equals(operands.get(0))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag || phiInst.getUseList().isEmpty()) {
                        Value value = operands.get(0);
                        block.getInstructions().remove(phiInst);
                        phiInst.replaceByNewValue(value);
                        phiInst.deleteUse();
                    }
                }
            }
        }
    }
}