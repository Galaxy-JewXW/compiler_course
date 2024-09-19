package optimize;

import middle.Module;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.instructions.AllocInst;
import middle.component.instructions.Instruction;
import middle.component.instructions.LoadInst;
import middle.component.instructions.PhiInst;
import middle.component.instructions.StoreInst;
import middle.component.model.Use;
import middle.component.model.Value;
import middle.component.types.IntegerType;

import java.util.HashSet;
import java.util.Stack;

public class Mem2Reg {
    // 当前正在处理的分配指令
    private static Instruction curAllocInst;
    // 所有使用当前变量的指令
    private static HashSet<Instruction> varUses;
    // 所有定义当前变量的指令
    private static HashSet<Instruction> varDefs;
    // 所有包含变量定义的基本块
    private static HashSet<BasicBlock> defBlocks;
    // 用栈来维护当前变量的最新定义
    private static Stack<Value> reachDef;

    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            getJoinSet(function);
            runImmediateDominator(function);
            getDominanceFrontier(function);
        }
        for (Function function : module.getFunctions()) {
            BasicBlock first = function.getBasicBlocks().get(0);
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (instruction instanceof AllocInst allocInst
                            && (allocInst.getAllocType().equals(IntegerType.i32)
                            || (allocInst.getAllocType().equals(IntegerType.i8)))) {
                        makeInfo(instruction);
                    }
                }
            }
        }
    }

    private static void getJoinSet(Function function) {
        // 计算函数中每个基本块的支配关系
        getDominance(function);
    }

    // 由CFG构建支配关系
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

    // 计算直接支配者
    private static void getImmediateDominator(BasicBlock basicBlock) {
        // 创建一个新的集合，包含所有被basicBlock支配的基本块
        // 使用HashSet提高查找效率
        HashSet<BasicBlock> dominantees = new HashSet<>(basicBlock.getDominantees());
        // 从集合中移除basicBlock自身，因为一个块不能是自己的直接支配者
        dominantees.remove(basicBlock);
        // 遍历所有被basicBlock支配的基本块
        for (BasicBlock block : dominantees) {
            // 初始化immediateDOM为basicBlock
            BasicBlock immediateDOM = basicBlock;
            // 在所有被支配的块中寻找最接近的支配者
            for (BasicBlock potentialIdom : dominantees) {
                // 跳过block自身，并检查potentialIdom是否支配block
                if (potentialIdom != block && potentialIdom.dominant(block)) {
                    // 如果immediateDOM仍为basicBlock，或者potentialIdom支配当前的immediateDOM
                    // 则更新immediateDOM为potentialIdom
                    if (immediateDOM == basicBlock || potentialIdom.dominant(immediateDOM)) {
                        immediateDOM = potentialIdom;
                    }
                }
            }
            // 设置找到的最接近的支配者为block的直接支配者
            block.setImmediateDominator(immediateDOM);
        }
    }

    // 计算支配边界
    private static void getDominanceFrontier(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            for (BasicBlock basicBlock1 : basicBlock.getNextBlocks()) {
                BasicBlock block = basicBlock;
                while (!block.strictDominant(basicBlock1)) {
                    block.addDominantFrontier(basicBlock1);
                    block = block.getImmediateDominator();
                }
            }
        }
    }

    private static void makeInfo(Instruction instruction) {
        curAllocInst = instruction;
        varUses = new HashSet<>();
        varDefs = new HashSet<>();
        defBlocks = new HashSet<>();
        reachDef = new Stack<>();
        for (Use use : instruction.getUses()) {
            Instruction user = (Instruction) use.getUser();
            if (user instanceof StoreInst) {
                // store定义了变量的新值
                varDefs.add(user);
                defBlocks.add(user.getBasicBlock());
            } else if (user instanceof LoadInst) {
                // load代表变量的使用
                varUses.add(user);
            }
        }
    }

    private static void insertPhiInst() {
        HashSet<BasicBlock> f = new HashSet<>();
        Stack<BasicBlock> w = new Stack<>();
        for (BasicBlock basicBlock : defBlocks) {
            w.push(basicBlock);
        }
        while (!w.isEmpty()) {
            BasicBlock x = w.pop();
            for (BasicBlock y : x.getDominantFrontier()) {
                if (!f.contains(y)) {
                    f.add(y);
                    if (!defBlocks.contains(y)) {
                        w.push(y);
                    }
                }
            }
        }
    }

    private static void insertPhiInstToBlock(BasicBlock block) {
        PhiInst phiInst = new PhiInst(IntegerType.i32);
        phiInst.setBasicBlock(block);
        block.getInstructions().add(0, phiInst);
        varUses.add(phiInst);
        varDefs.add(phiInst);
    }
}
