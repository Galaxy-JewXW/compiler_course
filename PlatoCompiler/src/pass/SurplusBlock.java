package pass;

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
    private static HashSet<BasicBlock> visited;

    public static void build(Module module) {
        for (Function func : module.getFunctions()) {
            simplifyFunction(func);
        }
    }

    private static void simplifyFunction(Function func) {
        func.getBasicBlocks().forEach(SurplusBlock::deleteDeadInstr);
        visited = new HashSet<>();
        dfs(func.getEntryBlock());
        func.getBasicBlocks().removeIf(block -> {
            if (!visited.contains(block)) {
                block.getInstructions().forEach(Instruction::removeOperands);
                block.removeOperands();
                block.setDeleted(true);
                return true;
            }
            return false;
        });
    }

    private static void deleteDeadInstr(BasicBlock block) {
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

    private static void dfs(BasicBlock block) {
        if (!visited.add(block)) {
            return;
        }
        Instruction lastInstruction = block.getLastInstruction();
        if (lastInstruction instanceof BrInst brInst) {
            if (brInst.isConditional()) {
                dfs(brInst.getTrueBlock());
                dfs(brInst.getFalseBlock());
            } else {
                dfs(brInst.getTrueBlock());
            }
        }
    }
}
