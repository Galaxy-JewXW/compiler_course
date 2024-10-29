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
import java.util.Stack;

public class GCM {
    private static Function currentFunction = null;
    private static HashSet<Instruction> visitedInstructions = null;

    public static void run(Module module) {
        Mem2Reg.run(module, false);
        module.getFunctions().forEach(GCM::optimize);
    }

    private static void optimize(Function function) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        currentFunction = function;
        visitedInstructions = new HashSet<>();
        ArrayList<BasicBlock> postOrderList = getPostOrder(function);
        Collections.reverse(postOrderList);
        postOrderList.forEach(basicBlock
                -> instructions.addAll(basicBlock.getInstructions()));
        for (Instruction instruction : instructions) {
            if (visitedInstructions.contains(instruction) || isPinned(instruction)) {
                continue;
            }
            scheduleEarly(instruction);
        }
        visitedInstructions = new HashSet<>();
        Collections.reverse(instructions);
        for (Instruction instruction : instructions) {
            if (visitedInstructions.contains(instruction) || isPinned(instruction)) {
                continue;
            }
            scheduleLate(instruction);
        }
    }

    public static ArrayList<BasicBlock> getPostOrder(Function function) {
        ArrayList<BasicBlock> postOrder = new ArrayList<>();
        Stack<BasicBlock> stack = new Stack<>();
        HashSet<BasicBlock> visited = new HashSet<>();
        BasicBlock entry = function.getEntryBlock();
        stack.push(entry);
        while (!stack.isEmpty()) {
            BasicBlock current = stack.peek();
            // 如果当前节点未被访问过，则标记为已访问并将其子节点压入栈中
            if (!visited.contains(current)) {
                visited.add(current);
                // 逆序压入子节点，以确保按照原始顺序访问
                ArrayList<BasicBlock> children = new ArrayList<>(
                        current.getImmediateDominateBlocks());
                for (int i = children.size() - 1; i >= 0; i--) {
                    BasicBlock child = children.get(i);
                    if (!visited.contains(child)) {
                        stack.push(child);
                    }
                }
            } else {
                // 如果当前节点已被访问过，说明其所有子节点已被处理，添加到 postOrder 中
                stack.pop();
                postOrder.add(current);
            }
        }
        return postOrder;
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
        // TODO: 循环分析，实现select_block
        BasicBlock select = lca;
        while (lca != instruction.getBasicBlock()) {
            if (lca == null) {
                throw new RuntimeException();
            }
            lca = lca.getImmediateDominator();
        }
    }
}
