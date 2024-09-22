package optimize;

import middle.Module;
import middle.component.BasicBlock;
import middle.component.Function;

import java.util.HashSet;

// 删除不可达的基本块
// 没有任何路径可以到达它，可以认为所有路径都“假设性地”经过这个基本块，
// 这使得所有基本块都支配这个不可达的基本块
public class UnusedBasicBlock {
    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            HashSet<BasicBlock> visited = new HashSet<>();
            dfs(function.getBasicBlocks().get(0), visited);
            function.getBasicBlocks().removeIf(b -> !visited.contains(b));
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
