package pass.analyze;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.Terminator;

import java.util.ArrayList;
import java.util.HashSet;

// 删除多余的基本块
public class SurplusBlock {
    private static HashSet<BasicBlock> reachedBlocks;

    public static void build(Module module) {
        for (Function func : module.getFunctions()) {
            simplifyFunction(func);
        }
    }

    private static void simplifyFunction(Function func) {
        func.getBasicBlocks().forEach(SurplusBlock::deleteDeadInstr);
        reachedBlocks = new HashSet<>();
        findReachable(func.getBasicBlocks().get(0));
        func.getBasicBlocks().removeIf(block -> {
            if (!reachedBlocks.contains(block)) {
                block.getInstructions().forEach(Instruction::deleteUse);
                block.deleteUse();
                block.setDeleted(true);
                return true;
            }
            return false;
        });
    }

    public static void deleteDeadInstr(BasicBlock block) {
        ArrayList<Instruction> instructions = block.getInstructions();
        int terminatorIndex = findTerminatorIndex(instructions);
        if (terminatorIndex < instructions.size() - 1) {
            instructions.subList(terminatorIndex + 1, instructions.size()).clear();
        }
    }

    private static int findTerminatorIndex(ArrayList<Instruction> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i) instanceof Terminator) {
                return i;
            }
        }
        return instructions.size() - 1;
    }

    public static void findReachable(BasicBlock block) {
        if (!reachedBlocks.add(block)) {
            return;
        }
        Instruction lastInstruction = block.getLastInstruction();
        if (lastInstruction instanceof BrInst brInst) {
            if (brInst.isConditional()) {
                findReachable(brInst.getTrueBlock());
                findReachable(brInst.getFalseBlock());
            } else {
                findReachable(brInst.getTrueBlock());
            }
        }
    }
}
