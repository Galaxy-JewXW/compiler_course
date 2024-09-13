package pass.Analysis;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.instructions.ConvInst;
import IR.values.instructions.Instruction;

import java.util.Iterator;

public class DelRedundantInst {
    private IRModule irModule;

    public DelRedundantInst(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            for (BasicBlock bb : function.getBasicBlocks()) {
                Iterator<Instruction> it = bb.getInstructions().iterator();
                while (it.hasNext()) {
                    Instruction instr = it.next();
                    if (instr instanceof ConvInst convInst) {
                        instr.replacedByNewVal(convInst.getValue());
                        instr.deleteUse();
                        it.remove();
                    }
                }
            }
        }
    }
}
