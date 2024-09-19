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
        }
    }
}
