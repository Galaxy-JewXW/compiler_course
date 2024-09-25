package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.Terminator;

import java.util.HashSet;
import java.util.Iterator;

public class UnusedBasicBlock {
    public static void run(Module module) {
        delInstAfterTerminate(module);
        delUnusedBasicBlock(module);
    }

    public static void delInstAfterTerminate(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                boolean shouldRemove = false;
                Iterator<Instruction> iterator
                        = basicBlock.getInstructions().iterator();
                while (iterator.hasNext()) {
                    Instruction instruction = iterator.next();
                    if (shouldRemove) {
                        iterator.remove();
                    }
                    if (instruction instanceof Terminator) {
                        shouldRemove = true;
                    }
                }
            }
        }
    }

    public static void delUnusedBasicBlock(Module module) {
        for (Function function : module.getFunctions()) {
            HashSet<BasicBlock> visited = new HashSet<>();
            dfs(function.getBasicBlocks().get(0), visited);
            Iterator<BasicBlock> iterator = function.getBasicBlocks().iterator();
            while (iterator.hasNext()) {
                BasicBlock basicBlock = iterator.next();
                if (!visited.contains(basicBlock)) {
                    basicBlock.setDeleted(true);
                    iterator.remove();
                }
            }
        }
    }

    private static void dfs(BasicBlock block, HashSet<BasicBlock> visited) {
        visited.add(block);
        for (BasicBlock next : block.getNextBlocks()) {
            if (!visited.contains(next)) {
                dfs(next, visited);
            }
        }
    }
}
