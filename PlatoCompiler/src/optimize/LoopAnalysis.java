package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;

import java.util.*;

public class LoopAnalysis {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                block.setLoopRecord(null);
            }
            analyzeLoopForFunction(function);
        }
    }

    private static void analyzeLoopForFunction(Function function) {
        ArrayList<BasicBlock> postOrderBlocks = function.getPostOrder();
        for (BasicBlock entryBlock : postOrderBlocks) {
            ArrayList<BasicBlock> loopEndBlocks = findLoopEndBlocks(entryBlock);
            if (!loopEndBlocks.isEmpty()) {
                LoopRecord loop = new LoopRecord(entryBlock, loopEndBlocks);
                assignParentLoops(loop, loopEndBlocks);
            }
        }
        assignLoopDepths(function);
    }

    private static ArrayList<BasicBlock> findLoopEndBlocks(BasicBlock block) {
        ArrayList<BasicBlock> loopEndBlocks = new ArrayList<>();
        for (BasicBlock parent : block.getPrevBlocks()) {
            if (block.getDominateBlocks().contains(parent)) {
                loopEndBlocks.add(parent);
            }
        }
        return loopEndBlocks;
    }

    private static void assignParentLoops(LoopRecord loop, List<BasicBlock> loopEndBlocks) {
        Deque<BasicBlock> queue = new ArrayDeque<>(loopEndBlocks);
        while (!queue.isEmpty()) {
            BasicBlock currentBlock = queue.poll();
            LoopRecord existingLoop = currentBlock.getLoopRecord();
            if (existingLoop == null) {
                currentBlock.setLoopRecord(loop);
                if (!currentBlock.equals(loop.getEntry())) {
                    queue.addAll(currentBlock.getPrevBlocks());
                }
            } else {
                LoopRecord topmostParent = existingLoop;
                while (topmostParent.getParent() != null) {
                    topmostParent = topmostParent.getParent();
                }
                if (topmostParent != loop) {
                    topmostParent.setParent(loop);
                    for (BasicBlock parentBlock : topmostParent.getEntry().getPrevBlocks()) {
                        if (parentBlock.getLoopRecord() != topmostParent) {
                            queue.add(parentBlock);
                        }
                    }
                }
            }
        }
    }

    private static void assignLoopDepths(Function function) {
        Set<BasicBlock> visited = new HashSet<>();
        Deque<BasicBlock> stack = new ArrayDeque<>();
        BasicBlock entryBlock = function.getEntryBlock();
        stack.push(entryBlock);
        while (!stack.isEmpty()) {
            BasicBlock current = stack.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            LoopRecord currentLoop = current.getLoopRecord();
            if (currentLoop != null && current.equals(currentLoop.getEntry())) {
                int depth = calculateLoopDepth(currentLoop);
                currentLoop.setLoopDepth(depth);
            }
            for (BasicBlock child : current.getNextBlocks()) {
                if (!visited.contains(child)) {
                    stack.push(child);
                }
            }
        }
    }

    /**
     * Calculates the depth of the given loop by traversing its parent loops.
     *
     * @param loop The loop whose depth is to be calculated.
     * @return The depth of the loop.
     */
    private static int calculateLoopDepth(LoopRecord loop) {
        int depth = 1;
        LoopRecord parentLoop = loop.getParent();
        while (parentLoop != null) {
            depth++;
            parentLoop = parentLoop.getParent();
        }
        return depth;
    }
}
