package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.Undefined;
import middle.component.instruction.*;
import middle.component.model.Use;
import middle.component.model.User;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.*;

public class Mem2Reg {
    private static Function currentFunction;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> childBlocks;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> parentBlocks;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> dominatedBy;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> dominates;
    private static HashMap<BasicBlock, BasicBlock> immediateDominator;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> immediatelyDominates;

    private static AllocInst currentAlloc;
    private static ArrayList<Instruction> defInstructions;
    private static ArrayList<Instruction> useInstructions;
    private static ArrayList<BasicBlock> defBlocks;
    private static ArrayList<BasicBlock> useBlocks;
    private static Stack<Value> defStack;

    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            optimize(function);
        }
    }

    private static void optimize(Function function) {
        currentFunction = function;
        calcControlFlowGraph();
        calcDominatorTree();
        calcDominanceFrontier();

        for (BasicBlock block : function.getBasicBlocks()) {
            ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
            for (Instruction instruction : instructions) {
                if (instruction instanceof AllocInst allocInst
                        && (allocInst.getTargetType().equals(IntegerType.i32)
                        || allocInst.getTargetType().equals(IntegerType.i8))) {
                    currentAlloc = allocInst;
                    initMem2Reg();
                    insertPhi();
                    renameVariables(currentFunction.getEntryBlock());
                }
            }
        }
    }

    private static void calcControlFlowGraph() {
        childBlocks = new HashMap<>();
        parentBlocks = new HashMap<>();
        dominatedBy = new HashMap<>();
        dominates = new HashMap<>();
        immediateDominator = new HashMap<>();
        immediatelyDominates = new HashMap<>();
        for (BasicBlock block : currentFunction.getBasicBlocks()) {
            childBlocks.put(block, new ArrayList<>());
            parentBlocks.put(block, new ArrayList<>());
            dominates.put(block, new ArrayList<>());
            dominatedBy.put(block, new ArrayList<>());
            immediatelyDominates.put(block, new ArrayList<>());
        }
        for (BasicBlock block : currentFunction.getBasicBlocks()) {
            Instruction lastInstruction = block.getLastInstruction();
            if (lastInstruction instanceof BrInst brInst) {
                if (brInst.isConditional()) {
                    addEdge(block, brInst.getTrueBlock());
                    addEdge(block, brInst.getFalseBlock());
                } else {
                    addEdge(block, brInst.getTrueBlock());
                }
            }
        }
        for (BasicBlock block : currentFunction.getBasicBlocks()) {
            block.setNextBlocks(childBlocks.get(block));
            block.setPrevBlocks(parentBlocks.get(block));
        }
    }

    private static void addEdge(BasicBlock from, BasicBlock to) {
        childBlocks.get(from).add(to);
        parentBlocks.get(to).add(from);
    }

    private static void calcDominatorTree() {
        BasicBlock entry = currentFunction.getBasicBlocks().get(0);
        for (BasicBlock dominator : currentFunction.getBasicBlocks()) {
            Set<BasicBlock> reachableBlocks = new HashSet<>();
            dfs(entry, dominator, reachableBlocks);
            for (BasicBlock block : currentFunction.getBasicBlocks()) {
                if (!reachableBlocks.contains(block)) {
                    dominates.get(dominator).add(block);
                    dominatedBy.get(block).add(dominator);
                }
            }
            dominator.setDominateBlocks(dominates.get(dominator));
        }
        for (BasicBlock dominated : currentFunction.getBasicBlocks()) {
            for (BasicBlock dominator : dominatedBy.get(dominated)) {
                if (isImmediateDominator(dominator, dominated)) {
                    dominated.setImmediateDominator(dominator);
                    immediateDominator.put(dominated, dominator);
                    immediatelyDominates.get(dominator).add(dominated);
                    break;
                }
            }
        }
        for (BasicBlock block : currentFunction.getBasicBlocks()) {
            block.setImmediateDominateBlocks(immediatelyDominates.get(block));
        }
        calcDominatorTreeDepth(entry, 0);
    }

    private static void dfs(BasicBlock current, BasicBlock dominator, Set<BasicBlock> reachableBlocks) {
        reachableBlocks.add(current);
        if (current.equals(dominator)) {
            return;
        }
        for (BasicBlock child : current.getNextBlocks()) {
            if (!reachableBlocks.contains(child)) {
                dfs(child, dominator, reachableBlocks);
            }
        }
    }

    private static boolean isImmediateDominator(BasicBlock dominator, BasicBlock dominated) {
        for (BasicBlock otherDominator : dominatedBy.get(dominated)) {
            if (otherDominator != dominator
                    && dominates.get(dominator).contains(otherDominator)) {
                return false;
            }
        }
        return true;
    }

    private static void calcDominatorTreeDepth(BasicBlock block, int depth) {
        block.setImdomDepth(depth);
        for (BasicBlock dominated : block.getImmediateDominateBlocks()) {
            calcDominatorTreeDepth(dominated, depth + 1);
        }
    }

    private static void calcDominanceFrontier() {
        for (BasicBlock dominator : currentFunction.getBasicBlocks()) {
            ArrayList<BasicBlock> frontier = new ArrayList<>();
            for (BasicBlock dominated : dominator.getDominateBlocks()) {
                for (BasicBlock child : dominated.getNextBlocks()) {
                    if (!dominator.getDominateBlocks().contains(child)
                            && !frontier.contains(child)) {
                        frontier.add(child);
                    }
                }
            }
            for (BasicBlock child : dominator.getNextBlocks()) {
                if (!dominator.getDominateBlocks().contains(child)
                        && !frontier.contains(child)) {
                    frontier.add(child);
                }
            }
            dominator.setDominanceFrontier(frontier);
        }
    }

    private static void initMem2Reg() {
        useBlocks = new ArrayList<>();
        useInstructions = new ArrayList<>();
        defBlocks = new ArrayList<>();
        defInstructions = new ArrayList<>();
        defStack = new Stack<>();

        for (Use use : currentAlloc.getUseList()) {
            User user = use.getUser();
            Instruction instruction = (Instruction) user;
            if (instruction instanceof LoadInst && !instruction.getBasicBlock().isDeleted()) {
                useInstructions.add(instruction);
                if (!useBlocks.contains(instruction.getBasicBlock())) {
                    useBlocks.add(instruction.getBasicBlock());
                }
            }
            if (instruction instanceof StoreInst && !instruction.getBasicBlock().isDeleted()) {
                defInstructions.add(instruction);
                if (!defBlocks.contains(instruction.getBasicBlock())) {
                    defBlocks.add(instruction.getBasicBlock());
                }
            }
        }
    }

    private static void insertPhi() {
        HashSet<BasicBlock> F = new HashSet<>();
        ArrayList<BasicBlock> W = new ArrayList<>(defBlocks);
        while (!W.isEmpty()) {
            BasicBlock X = W.remove(0);
            for (BasicBlock Y : X.getDominanceFrontier()) {
                if (!F.contains(Y)) {
                    PhiInst phiInst = new PhiInst(currentAlloc.getTargetType(),
                            Y, new ArrayList<>(Y.getPrevBlocks()));
                    Y.getInstructions().add(0, phiInst);
                    useInstructions.add(phiInst);
                    defInstructions.add(phiInst);
                    F.add(Y);
                    if (!defBlocks.contains(Y)) {
                        W.add(Y);
                    }
                }
            }
        }
    }

    private static void renameVariables(BasicBlock block) {
        Iterator<Instruction> it = block.getInstructions().iterator();
        int pushCount = 0;
        while (it.hasNext()) {
            Instruction instruction = it.next();
            if (instruction.equals(currentAlloc)) {
                it.remove();
            } else if (instruction instanceof LoadInst
                    && useInstructions.contains(instruction)) {
                Value newValue = defStack.empty() ? new Undefined() : defStack.peek();
                instruction.replaceByNewValue(newValue);
                it.remove();
            } else if (instruction instanceof StoreInst storeInst
                    && defInstructions.contains(instruction)) {
                defStack.push(storeInst.getStoredValue());
                instruction.deleteUse();
                pushCount++;
                it.remove();
            } else if (instruction instanceof PhiInst
                    && defInstructions.contains(instruction)) {
                pushCount++;
                defStack.push(instruction);
            }
        }
        for (BasicBlock child : block.getNextBlocks()) {
            Instruction firstInstruction = child.getFirstInstruction();
            if (firstInstruction instanceof PhiInst phiInst && useInstructions.contains(firstInstruction)) {
                Value value = defStack.empty() ? new Undefined() : defStack.peek();
                phiInst.addValue(block, value);
            }
        }
        for (BasicBlock dominated : block.getImmediateDominateBlocks()) {
            renameVariables(dominated);
        }
        for (int i = 0; i < pushCount; i++) {
            defStack.pop();
        }
    }

}