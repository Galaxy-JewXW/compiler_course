package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.Iterator;

public class OptimizePhi {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                Iterator<Instruction> iterator = block.getInstructions().iterator();
                while (iterator.hasNext()) {
                    Instruction instruction = iterator.next();
                    if (!(instruction instanceof PhiInst phiInst)) {
                        break;
                    }
                    ArrayList<Value> operands = phiInst.getOperands();
                    for (int i = 0; i <= operands.size() - 1; i++) {
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
                        iterator.remove();
                        Value value = operands.get(0);
                        phiInst.replaceByNewValue(value);
                    }
                }
            }
        }
    }
}
