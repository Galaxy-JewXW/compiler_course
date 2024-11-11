package optimize;

import middle.IRData;
import middle.component.BasicBlock;
import middle.component.ConstString;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.io.OutputInst;
import middle.component.instruction.io.PutstrInst;

import java.util.ArrayList;

public class PrintOptimize {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                optimize(block);
            }
        }
    }

    private static void optimize(BasicBlock block) {
        boolean changed = true;
        while (changed) {
            changed = false;
            ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
            for (int i = 0; i < instructions.size(); i++) {
                Instruction instruction = instructions.get(i);
                if (!(instruction instanceof OutputInst outputInst)) {
                    continue;
                }
                if (!outputInst.constContent()) {
                    continue;
                }
                ArrayList<Instruction> buffer = new ArrayList<>();
                buffer.add((Instruction) outputInst);
                int j = i + 1;
                for (; j < instructions.size(); j++) {
                    if (instructions.get(j) instanceof OutputInst temp && temp.constContent()) {
                        buffer.add((Instruction) temp);
                    } else {
                        break;
                    }
                }
                if (buffer.size() < 2) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (Instruction output : buffer) {
                    sb.append(((OutputInst) output).getConstContent());
                }
                String output = sb.toString();
                output = output.replace("\\0A", "\n");
                ConstString constString = new ConstString(IRData.getConstStringName(), output);
                PutstrInst putstr = new PutstrInst(constString);
                block.getInstructions().set(i, putstr);
                block.getInstructions().removeAll(buffer);
                changed = true;
                break;
            }
        }
    }
}
