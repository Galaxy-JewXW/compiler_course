package pass;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.Value;
import IR.values.instructions.CallInst;
import IR.values.instructions.Instruction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class DelDeadCode {
    private final IRModule irModule;

    public DelDeadCode(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            delUnusedCode(function);
        }
    }

    private void delUnusedFunc() {

    }

    private void delUnusedCode(Function function) {
        HashSet<Instruction> usedInstr = new HashSet<>();
        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction instr : bb.getInstructions()) {
                if (useful(instr)) {
                    findUsefulClosure(instr, usedInstr);
                }
            }
        }
        for (BasicBlock bb : function.getBasicBlocks()) {
            Iterator<Instruction> it = bb.getInstructions().iterator();
            while (it.hasNext()) {
                Instruction instr = it.next();
                if (!usedInstr.contains(instr)) {
                    instr.deleteUse();
                    it.remove();
                }
            }
        }
    }

    private void findUsefulClosure(Instruction entry, HashSet<Instruction> usedInstr) {
        Stack<Instruction> stack = new Stack<>();
        stack.push(entry);
        while (!stack.isEmpty()) {
            Instruction x = stack.pop();
            if (usedInstr.contains(x)) {
                continue;
            }
            usedInstr.add(x);
            for (Value value : x.getOperands()) {
                if (value instanceof Instruction instr) {
                    stack.push(instr);
                }
            }
        }
    }

    private boolean useful(Instruction instr) {
        return instr.getName().isEmpty() || instr instanceof CallInst;
    }
}
