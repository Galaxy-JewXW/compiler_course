package pass;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class OptimizePhi {
    public static void run(Module module) {
        module.getFunctions().forEach(OptimizePhi::optimizeFunction);
    }

    private static void optimizeFunction(Function function) {
        function.getBasicBlocks().forEach(OptimizePhi::optimizeBasicBlock);
    }

    private static void optimizeBasicBlock(BasicBlock block) {
        List<Instruction> instructions = new ArrayList<>(block.getInstructions());
        for (Instruction instruction : instructions) {
            if (!(instruction instanceof PhiInst phiInst)) {
                break;
            }
            optimizePhiInstruction(block, phiInst);
        }
    }

    private static void optimizePhiInstruction(BasicBlock block, PhiInst phiInst) {
        ArrayList<Value> operands = phiInst.getOperands();
        ArrayList<BasicBlock> phiBlocks = phiInst.getBlocks();

        // Remove operands and blocks for deleted blocks
        removeDeletedBlocks(operands, phiBlocks);

        if (allOperandsEqual(operands) || phiInst.getUserList().isEmpty()) {
            Value value = operands.get(0);
            block.getInstructions().remove(phiInst);
            phiInst.replaceByNewValue(value);
            phiInst.removeOperands();
        }
    }

    private static void removeDeletedBlocks(ArrayList<Value> operands,
                                            ArrayList<BasicBlock> phiBlocks) {
        ListIterator<BasicBlock> blockIterator = phiBlocks.listIterator(phiBlocks.size());
        ListIterator<Value> operandIterator = operands.listIterator(operands.size());

        while (blockIterator.hasPrevious() && operandIterator.hasPrevious()) {
            if (blockIterator.previous().isDeleted()) {
                blockIterator.remove();
                operandIterator.previous();
                operandIterator.remove();
            } else {
                operandIterator.previous();
            }
        }
    }

    private static boolean allOperandsEqual(ArrayList<Value> operands) {
        return operands.stream().allMatch(operand -> operand.equals(operands.get(0)));
    }
}