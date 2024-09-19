package optimize;

import middle.Module;
import middle.component.BasicBlock;
import middle.component.Function;

import java.util.HashSet;

public class Mem2Reg {
    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            getJoinSet(function);
        }
    }

    private static void getJoinSet(Function function) {
        // 计算函数中每个基本块的支配关系
        getDominance(function);
    }

    private static void getDominance(Function function) {
        BasicBlock firstBlock = function.getBasicBlocks().get(0);
        for (BasicBlock targetBlock : function.getBasicBlocks()) {
            HashSet<BasicBlock> visited = new HashSet<>();
            visit(firstBlock, targetBlock, visited);
            HashSet<BasicBlock> dominatee = new HashSet<>();
            for (BasicBlock block : function.getBasicBlocks()) {
                if (!visited.contains(block)) {
                    dominatee.add(block);
                }
            }
            targetBlock.setDominantees(dominatee);
        }
    }

    private static void visit(BasicBlock block, BasicBlock target, HashSet<BasicBlock> visited) {
        if (target.equals(block)) {
            return;
        }
        visited.add(block);
        for (BasicBlock basicBlock : block.getNextBlocks()) {
            if (!visited.contains(basicBlock)) {
                visit(basicBlock, target, visited);
            }
        }
    }

    private static void runImmediateDominator(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            getImmediateDominator(basicBlock);
        }
    }

    private static void getImmediateDominator(BasicBlock basicBlock) {
        // 计算由basicBlock支配的基本块中，哪些属于直接支配
        for (BasicBlock block : basicBlock.getDominantees()) {
            // 最近的严格支配，不能是自己
            if (block.equals(basicBlock)) {
                continue;
            }
            boolean flag = true;
            for (BasicBlock block1 : basicBlock.getDominantees()) {
                if (block1.equals(basicBlock)) {
                    continue;
                }
                if (block1.dominant(block)) {
                    // 此时block1与basicBlock相比距离block更近
                    flag = false;
                    break;
                }
            }
            if (flag) {
                block.setImmediateDominator(basicBlock);
            }
        }

    }
}
