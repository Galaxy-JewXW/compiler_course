package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.*;
import middle.component.model.Value;
import middle.component.type.PointerType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class GCM {
    private static Function currentFunction = null;
    private static HashSet<Instruction> visitedInstructions = null;

    public static void run(Module module) {
        Mem2Reg.run(module, false);
        LoopAnalysis.run(module);
        module.getFunctions().forEach(GCM::optimize);
    }

    private static void optimize(Function function) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        currentFunction = function;
        visitedInstructions = new HashSet<>();
        ArrayList<BasicBlock> postOrderList = function.getPostOrder();
        Collections.reverse(postOrderList);
        postOrderList.forEach(basicBlock
                -> instructions.addAll(basicBlock.getInstructions()));
        for (Instruction instruction : instructions) {
            scheduleEarly(instruction);
        }
        visitedInstructions = new HashSet<>();
        Collections.reverse(instructions);
        for (Instruction instruction : instructions) {
            scheduleLate(instruction);
        }
    }

    private static boolean isPinned(Instruction instruction) {
        if (instruction instanceof BinaryInst
                || instruction instanceof GepInst
                || instruction instanceof ZextInst
                || instruction instanceof TruncInst) {
            return false;
        }

        if (instruction instanceof CallInst callInst) {
            Function calledFunc = callInst.getCalledFunction();
            if (calledFunc.hasSideEffects()
                    || instruction.getBasicBlock().getFunction().equals(calledFunc)) {
                return true;
            }
            if (callInst.getUserList().isEmpty()) {
                return true;
            }
            return callInst.getUserList().stream().anyMatch(user ->
                    user instanceof GepInst || user instanceof LoadInst
                            || user.getValueType() instanceof PointerType);
        }

        return true;
    }

    private static void scheduleEarly(Instruction instruction) {
        if (visitedInstructions.contains(instruction) || isPinned(instruction)) {
            return;
        }
        visitedInstructions.add(instruction);
        BasicBlock entry = currentFunction.getEntryBlock();
        instruction.getBasicBlock().getInstructions().remove(instruction);
        entry.getInstructions().add(entry.getInstructions().size() - 1, instruction);
        instruction.setBasicBlock(entry);
        for (Value operand : instruction.getOperands()) {
            if (operand instanceof Instruction instruction1) {
                scheduleEarly(instruction1);
                if (instruction.getBasicBlock().getImdomDepth() < instruction1.getBasicBlock().getImdomDepth()) {
                    instruction.getBasicBlock().getInstructions().remove(instruction);
                    BasicBlock block = instruction1.getBasicBlock();
                    block.getInstructions().add(block.getInstructions().size() - 1, instruction);
                    instruction.setBasicBlock(block);
                }
            }
        }
    }

    private static BasicBlock getLCA(BasicBlock block1, BasicBlock block2) {
        if (block1 == null) {
            return block2;
        }
        while (block1.getImdomDepth() < block2.getImdomDepth()) {
            block2 = block2.getImmediateDominator();
        }
        while (block2.getImdomDepth() < block1.getImdomDepth()) {
            block1 = block1.getImmediateDominator();
        }
        while (!block1.equals(block2)) {
            block1 = block1.getImmediateDominator();
            block2 = block2.getImmediateDominator();
        }
        return block1;
    }

    private static void scheduleLate(Instruction instruction) {
        if (visitedInstructions.contains(instruction) || isPinned(instruction)) {
            return;
        }
        visitedInstructions.add(instruction);
        BasicBlock lca = null;
        for (Value value : instruction.getUserList()) {
            if (!(value instanceof Instruction instruction1)) {
                continue;
            }
            scheduleLate(instruction1);
            if (instruction1 instanceof PhiInst phiInst) {
                for (int i = 0; i < phiInst.getOperands().size(); i++) {
                    Value operand = phiInst.getOperands().get(i);
                    if (operand == instruction) {
                        lca = getLCA(lca, phiInst.getBlocks().get(i));
                    }
                }
            } else {
                lca = getLCA(lca, instruction1.getBasicBlock());
            }
        }
        BasicBlock select = lca;
        while (lca != instruction.getBasicBlock()) {
            if (lca == null) {
                throw new RuntimeException();
            }
            lca = lca.getImmediateDominator();
            if (lca.getLoopDepth() < select.getLoopDepth()) {
                select = lca;
            }
        }
        if (lca == null) {
            throw new RuntimeException();
        }
        instruction.getBasicBlock().getInstructions().remove(instruction);
        select.getInstructions().add(select.getInstructions().size() - 1, instruction);
        instruction.setBasicBlock(select);
        for (Instruction instruction1 : select.getInstructions()) {
            if (instruction1 != instruction && !(instruction1 instanceof PhiInst)
                    && instruction1.getOperands().contains(instruction)) {
                select.getInstructions().remove(instruction);
                select.getInstructions().add(select.getInstructions().indexOf(instruction1), instruction);
                break;
            }
        }
    }
}
