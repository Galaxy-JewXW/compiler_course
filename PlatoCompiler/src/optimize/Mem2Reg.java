package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.Undefined;
import middle.component.instruction.*;
import middle.component.model.Use;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Mem2Reg {
    // 当前正在处理的分配指令
    private static AllocInst curAllocInst;
    // 所有使用当前变量的指令
    private static HashSet<Instruction> varUses;
    // 所有定义当前变量的指令
    private static HashSet<Instruction> varDefs;
    // 所有包含变量定义的基本块
    private static HashSet<BasicBlock> defBlocks;
    // 用栈来维护当前变量的最新定义
    private static Stack<Value> reachDef;

    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            analyze(function);
            transform(function);
        }
    }

    // 对函数的基本块进行支配分析
    private static void analyze(Function function) {
        calculateDominators(function);
        calculateAllImmediateDominators(function);
        calculateDominanceFrontier(function);
    }

    // 计算函数中每个基本块的支配者
    private static void calculateDominators(Function function) {
        BasicBlock entryBlock = function.getBasicBlocks().get(0);
        for (BasicBlock target : function.getBasicBlocks()) {
            HashSet<BasicBlock> visited = new HashSet<>();
            dfs(target, entryBlock, visited);
            HashSet<BasicBlock> dominatedBlocks =
                    new HashSet<>(function.getBasicBlocks());
            dominatedBlocks.removeAll(visited);
            target.setDominatedBlocks(dominatedBlocks);
        }
    }

    // dfs搜索所有不被target支配的基本块
    private static void dfs(BasicBlock target, BasicBlock curBlock, HashSet<BasicBlock> visited) {
        if (curBlock.equals(target)) {
            return;
        }
        visited.add(curBlock);
        for (BasicBlock next : curBlock.getNextBlocks()) {
            if (!visited.contains(next)) {
                dfs(target, next, visited);
            }
        }
    }

    private static void calculateAllImmediateDominators(Function function) {
        for (BasicBlock dominator : function.getBasicBlocks()) {
            for (BasicBlock block : dominator.getDominatedBlocks()) {
                if (block.equals(dominator)) {
                    continue;
                }
                boolean isImmediateDominator = true;
                for (BasicBlock otherBlock : dominator.getDominatedBlocks()) {
                    if (otherBlock.equals(dominator)) {
                        continue;
                    }
                    if (otherBlock.strictDominant(block)) {
                        isImmediateDominator = false;
                        break;
                    }
                }
                if (isImmediateDominator) {
                    block.setImmediateDominator(dominator);
                }
            }
        }
    }

    private static void calculateDominanceFrontier(Function function) {
        for (BasicBlock block : function.getBasicBlocks()) {
            for (BasicBlock block1 : block.getNextBlocks()) {
                BasicBlock temp = block;
                while (!temp.strictDominant(block1)) {
                    temp.addDominantFrontier(block1);
                    temp = temp.getImmediateDominator();
                }
            }
        }
    }

    private static void transform(Function function) {
        BasicBlock entryBlock = function.getBasicBlocks().get(0);
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            ArrayList<Instruction> instructions =
                    new ArrayList<>(basicBlock.getInstructions());
            for (Instruction instruction : instructions) {
                if (instruction instanceof AllocInst allocInst
                        && (allocInst.getTargetType().equals(IntegerType.i32)
                        || allocInst.getTargetType().equals(IntegerType.i8))) {
                    setAttr(allocInst);
                    insertPhi();
                    renameVariables(entryBlock);
                }
            }
        }
    }

    private static void setAttr(AllocInst allocInst) {
        curAllocInst = allocInst;
        varUses = new HashSet<>();
        varDefs = new HashSet<>();
        defBlocks = new HashSet<>();
        reachDef = new Stack<>();
        for (Use use : allocInst.getUseList()) {
            Instruction user = (Instruction) use.getUser();
            if (user instanceof StoreInst) {
                // 存储指令定义了变量
                varDefs.add(user);
                defBlocks.add(user.getBasicBlock());
            } else if (user instanceof LoadInst) {
                // 加载指令使用了变量
                varUses.add(user);
            }
        }
    }

    private static void insertPhi() {
        // 需要添加phi的基本块的集合
        HashSet<BasicBlock> F = new HashSet<>();
        // 定义变量的基本块的集合
        Stack<BasicBlock> W = new Stack<>();
        // 将defBlocks中的基本块推入栈
        W.addAll(defBlocks);
        while (!W.isEmpty()) {
            BasicBlock X = W.pop();
            for (BasicBlock Y : X.getDominanceFrontier()) {
                if (!F.contains(Y)) {
                    PhiInst phiInst = new PhiInst(curAllocInst.getTargetType());
                    phiInst.setBasicBlock(Y);
                    Y.getInstructions().add(0, phiInst);
                    varUses.add(phiInst);
                    varDefs.add(phiInst);
                    F.add(Y);
                    if (!defBlocks.contains(Y)) {
                        W.push(Y);
                    }
                }
            }
        }
    }

    private static void renameVariables(BasicBlock entryBlock) {
        int cnt = 0;
        Iterator<Instruction> iter = entryBlock.getInstructions().iterator();
        while (iter.hasNext()) {
            Instruction instruction = iter.next();
            if (instruction.equals(curAllocInst)) {
                iter.remove();
            } else if (instruction instanceof LoadInst loadInst && varUses.contains(instruction)) {
                Value newValue = reachDef.isEmpty() ? new Undefined() : reachDef.peek();
                loadInst.replaceByNewValue(newValue);
                iter.remove();
            } else if (instruction instanceof StoreInst storeInst && varDefs.contains(instruction)) {
                reachDef.push(storeInst.getStoredValue());
                cnt++;
                storeInst.deleteUse();
                iter.remove();
            } else if (instruction instanceof PhiInst phiInst && varDefs.contains(instruction)) {
                reachDef.push(phiInst);
                cnt++;
            }
        }
        for (BasicBlock block : entryBlock.getNextBlocks()) {
            Instruction first = block.getInstructions().get(0);
            if (first instanceof PhiInst phiInst && varUses.contains(first)) {
                Value newVal = reachDef.isEmpty() ? new Undefined() : reachDef.peek();
                phiInst.addValue(entryBlock, newVal);
            }
        }
        for (BasicBlock block : entryBlock.getImmediateDominatedBlocks()) {
            renameVariables(block);
        }
        for (int i = 0; i < cnt; i++) {
            reachDef.pop();
        }
    }
}
