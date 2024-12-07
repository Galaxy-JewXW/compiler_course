package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockSimplify {
    public static void run(Module module) {
        // 初始的 Mem2Reg 传递
        Mem2Reg.run(module, false);

        boolean changed;
        int iteration = 0;
        final int MAX_ITERATIONS = 10; // 防止无限循环

        do {
            changed = false;
            Mem2Reg.run(module, false);
            // 遍历模块中的每个函数
            for (Function function : module.getFunctions()) {
                // 使用 DFS 重新排序基本块
                ArrayList<BasicBlock> orderedBlocks = reorderBasicBlocksDFS(function);
                if (!orderedBlocks.equals(function.getBasicBlocks())) {
                    function.setBasicBlocks(orderedBlocks);
                    changed = true;
                }
            }
            Mem2Reg.run(module, false);
            // 尝试合并基本块
            for (Function function : module.getFunctions()) {
                boolean merged = merge(function);
                if (merged) {
                    changed = true;
                }
            }
            // 如果进行了更改，运行 Mem2Reg
            if (changed) {
                Mem2Reg.run(module, false);
            }
            iteration++;
        } while (changed && iteration < MAX_ITERATIONS);

        // 最终的重新排序
        for (Function function : module.getFunctions()) {
            ArrayList<BasicBlock> orderedBlocks = reorderBasicBlocksDFS(function);
            function.setBasicBlocks(orderedBlocks);
            Mem2Reg.run(module, false);
        }
    }

    /**
     * 使用深度优先搜索（DFS）重新排序函数的基本块。
     *
     * @param function 需要重新排序基本块的函数
     * @return 重新排序后的基本块的 ArrayList
     */
    private static ArrayList<BasicBlock> reorderBasicBlocksDFS(Function function) {
        List<BasicBlock> blocks = function.getBasicBlocks();
        ArrayList<BasicBlock> orderedBlocks = new ArrayList<>();
        Set<BasicBlock> visited = new HashSet<>();
        Deque<BasicBlock> stack = new ArrayDeque<>();

        if (!blocks.isEmpty()) {
            stack.push(blocks.get(0)); // 假设第一个块是入口点
        }

        while (!stack.isEmpty()) {
            BasicBlock current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);
                orderedBlocks.add(current);

                // 逆序压栈以保持顺序
                List<BasicBlock> successors = getSuccessors(current);
                Collections.reverse(successors);
                for (BasicBlock succ : successors) {
                    if (!visited.contains(succ)) {
                        stack.push(succ);
                    }
                }
            }
        }

        // 将未在DFS中访问到的块追加到末尾
        for (BasicBlock block : blocks) {
            if (!visited.contains(block)) {
                orderedBlocks.add(block);
            }
        }

        return orderedBlocks;
    }

    /**
     * 获取给定基本块的后继基本块。
     *
     * @param block 需要获取后继块的基本块
     * @return 后继基本块的列表
     */
    private static List<BasicBlock> getSuccessors(BasicBlock block) {
        Instruction lastInst = block.getLastInstruction();
        if (lastInst instanceof BrInst brInst) {
            if (brInst.isConditional()) {
                return Arrays.asList(brInst.getTrueBlock(), brInst.getFalseBlock());
            } else {
                return Collections.singletonList(brInst.getTrueBlock());
            }
        }
        return Collections.emptyList();
    }

    /**
     * 尝试合并函数中的基本块。
     *
     * @param function 需要合并基本块的函数
     * @return 如果进行了合并操作，返回 true；否则，返回 false
     */
    private static boolean merge(Function function) {
        boolean merged = false;
        ArrayList<BasicBlock> blocks = new ArrayList<>(function.getBasicBlocks());

        // 使用迭代器安全地在遍历时移除块
        Iterator<BasicBlock> it = blocks.iterator();
        while (it.hasNext()) {
            BasicBlock block = it.next();
            if (block.isDeleted()) {
                continue;
            }
            if (block.getNextBlocks().size() == 1) {
                BasicBlock child = block.getNextBlocks().get(0);
                if (child.getPrevBlocks().size() == 1 && !child.isDeleted()) {
                    // 合并块和子块
                    performMerge(block, child);
                    merged = true;
                }
            }
        }

        // 如果进行了合并，更新函数的基本块列表
        if (merged) {
            // 过滤掉已删除的块
            ArrayList<BasicBlock> updatedBlocks = new ArrayList<>();
            for (BasicBlock block : blocks) {
                if (!block.isDeleted()) {
                    updatedBlocks.add(block);
                }
            }
            function.setBasicBlocks(updatedBlocks);
        }

        return merged;
    }

    /**
     * 合并两个基本块：父块和子块。
     *
     * @param parent 父基本块
     * @param child  要合并到父块中的子基本块
     */
    private static void performMerge(BasicBlock parent, BasicBlock child) {
        // 从父块中移除跳转指令
        Instruction jumpInstr = parent.getLastInstruction();
        jumpInstr.removeOperands();
        parent.getInstructions().remove(jumpInstr);

        // 将子块中的所有指令移动到父块
        Iterator<Instruction> it = child.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instr = it.next();
            if (instr instanceof PhiInst phiInst) {
                // 更新 phi 指令以使用来自父块的操作数
                int index = phiInst.getBlocks().indexOf(parent);
                if (index != -1) {
                    Value newValue = phiInst.getOperands().get(index);
                    phiInst.replaceByNewValue(newValue);
                }
                phiInst.removeOperands();
            } else {
                parent.getInstructions().add(instr);
                instr.setBasicBlock(parent);
            }
            it.remove();
        }

        // 将子块的引用重定向到父块，并标记子块为已删除
        child.replaceByNewValue(parent);
        child.setDeleted(true);
    }
}
