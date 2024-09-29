package pass;

import backend.enums.Register;
import middle.IRData;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.Module;
import middle.component.Undefined;
import middle.component.instruction.BrInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.MoveInst;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;
import java.util.HashMap;

public class RemovePhi {
    private static HashMap<Value, Register> var2reg;

    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            var2reg = new HashMap<>(function.getVar2reg());
            ArrayList<BasicBlock> blocks = new ArrayList<>(function.getBasicBlocks());
            for (BasicBlock b : blocks) {
                removePhi(b);
            }
        }
    }

    private static void removePhi(BasicBlock currentBlock) {
        ArrayList<Instruction> instructions
                = new ArrayList<>(currentBlock.getInstructions());
        HashMap<BasicBlock, ArrayList<MoveInst>> moves = new HashMap<>();
        for (BasicBlock parent : currentBlock.getPrevBlocks()) {
            moves.put(parent, new ArrayList<>());
        }
        for (Instruction instruction : instructions) {
            if (!(instruction instanceof PhiInst phiInst)) {
                break;
            }
            ArrayList<Value> alternatives = phiInst.getOperands();
            ArrayList<BasicBlock> blocks = phiInst.getBlocks();
            for (int i = 0; i < blocks.size(); i++) {
                if (!currentBlock.getPrevBlocks().contains(blocks.get(i))) {
                    blocks.remove(i);
                    alternatives.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < alternatives.size(); i++) {
                if (!(alternatives.get(i) instanceof Undefined)) {
                    moves.get(blocks.get(i)).add(new MoveInst(
                            phiInst, alternatives.get(i),
                            blocks.get(i)));
                }
            }
            currentBlock.getInstructions().remove(instruction);
        }
        ArrayList<BasicBlock> parents
                = new ArrayList<>(currentBlock.getPrevBlocks());
        for (BasicBlock parent : parents) {
            if (moves.get(parent).isEmpty()) {
                continue;
            }
            ArrayList<MoveInst> parallels = new ArrayList<>();
            ArrayList<MoveInst> moveList = moves.get(parent);
            for (int i = 0; i < moveList.size(); i++) {
                for (int j = i + 1; j < moveList.size(); j++) {
                    if (moveList.get(i).getToValue()
                            .equals(moveList.get(j).getFromValue())) {
                        Value value = new Value(IRData.getVarName(), IntegerType.i32);
                        MoveInst tempMove = new MoveInst(
                                value, moveList.get(i).getToValue(), currentBlock);
                        parallels.add(0, tempMove);
                        for (int k = j; k < moveList.size(); k++) {
                            if (moveList.get(i).getToValue().equals(moveList.get(k).getFromValue())) {
                                moveList.get(k).setFromValue(value);
                            }
                        }
                    }
                }
                parallels.add(moveList.get(i));
            }

            ArrayList<MoveInst> finalMoves = new ArrayList<>();
            for (int i = 0; i < parallels.size(); i++) {
                for (int j = i + 1; j < parallels.size(); j++) {
                    if (var2reg.containsKey(parallels.get(i).getToValue())
                            && var2reg.containsKey(parallels.get(j).getFromValue())
                            && var2reg.get(parallels.get(i).getToValue())
                            .equals(var2reg.get(parallels.get(j).getFromValue()))) {
                        Value value = new Value(IRData.getVarName(), IntegerType.i32);
                        MoveInst tempMove = new MoveInst(
                                value, moveList.get(i).getToValue(), currentBlock);
                        finalMoves.add(0, tempMove);
                        for (int k = j; k < parallels.size(); k++) {
                            if (var2reg.containsKey(parallels.get(k).getFromValue())
                                    && var2reg.get(parallels.get(i).getToValue())
                                    .equals(var2reg.get(parallels.get(k).getFromValue()))) {
                                parallels.get(k).setFromValue(value);
                            }
                        }
                    }
                }
                finalMoves.add(parallels.get(i));
            }

            if (parent.getNextBlocks().size() > 1) {
                BasicBlock newBlock = new BasicBlock(IRData.getVarName());
                newBlock.setFunction(currentBlock.getFunction());
                ArrayList<BasicBlock> blocks = currentBlock.getFunction().getBasicBlocks();
                blocks.add(blocks.indexOf(currentBlock), newBlock);
                for (Instruction instruction : finalMoves) {
                    newBlock.addInstruction(instruction);
                    instruction.setBasicBlock(newBlock);
                }
                BrInst brInst = new BrInst(currentBlock);
                brInst.setBasicBlock(newBlock);
                newBlock.addInstruction(brInst);
                BrInst brInst1 = (BrInst) parent.getInstructions()
                        .get(parent.getInstructions().size() - 1);
                brInst1.getOperands().set(brInst1.getOperands().indexOf(currentBlock), newBlock);
            } else {
                for (Instruction instruction : finalMoves) {
                    parent.getInstructions().add(
                            parent.getInstructions().size() - 1, instruction);
                }
            }
        }
    }
}
