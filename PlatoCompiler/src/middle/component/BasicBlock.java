package middle.component;

import middle.IRData;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Use;
import middle.component.model.User;
import middle.component.type.LabelType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BasicBlock extends User {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    // 支配边界
    private ArrayList<BasicBlock> dominanceFrontier = new ArrayList<>();
    // 自身直接支配的基本块，区分 支配 和 直接支配
    // 或支配树的子节点
    private ArrayList<BasicBlock> immediateDominateBlocks = new ArrayList<>();
    // 前驱基本块的集合
    private ArrayList<BasicBlock> prevBlocks = new ArrayList<>();
    // 后继基本块的集合
    private ArrayList<BasicBlock> nextBlocks = new ArrayList<>();
    private Function function;
    // 可支配的基本块的集合
    private ArrayList<BasicBlock> dominateBlocks = null;
    // 被直接支配的基本块，或支配树的根节点
    private BasicBlock immediateDominator = null;
    // 标记是否在优化过程中被删除
    private boolean isDeleted = false;
    private int imdomDepth;

    public BasicBlock(String name) {
        super(name, new LabelType());
        if (IRData.isInsect()) {
            this.function = IRData.getCurrentFunction();
            function.addBasicBlock(this);
        }
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    public Instruction getFirstInstruction() {
        return instructions.get(0);
    }

    public Instruction getLastInstruction() {
        return instructions.get(instructions.size() - 1);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public void updateId() {
        setName(IRData.getBlockName());
        for (Instruction instruction : instructions) {
            instruction.updateId();
        }
    }

    public void addPrevBlock(BasicBlock prevBlock) {
        prevBlocks.add(prevBlock);
    }

    public void addNextBlock(BasicBlock nextBlock) {
        nextBlocks.add(nextBlock);
    }

    public ArrayList<BasicBlock> getPrevBlocks() {
        return prevBlocks;
    }

    public void setPrevBlocks(ArrayList<BasicBlock> prevBlocks) {
        this.prevBlocks = new ArrayList<>(prevBlocks);
    }

    public ArrayList<BasicBlock> getNextBlocks() {
        return nextBlocks;
    }

    public void setNextBlocks(ArrayList<BasicBlock> nextBlocks) {
        this.nextBlocks = new ArrayList<>(nextBlocks);
    }

    public ArrayList<BasicBlock> getDominateBlocks() {
        return dominateBlocks;
    }

    public void setDominateBlocks(ArrayList<BasicBlock> dominateBlocks) {
        this.dominateBlocks = dominateBlocks;
    }

    public BasicBlock getImmediateDominator() {
        return immediateDominator;
    }

    public void setImmediateDominator(BasicBlock immediateDominator) {
        this.immediateDominator = immediateDominator;
    }

    public ArrayList<BasicBlock> getImmediateDominateBlocks() {
        return immediateDominateBlocks;
    }

    public void setImmediateDominateBlocks(ArrayList<BasicBlock> immediateDominateBlocks) {
        this.immediateDominateBlocks = new ArrayList<>(immediateDominateBlocks);
    }

    public ArrayList<BasicBlock> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public void setDominanceFrontier(ArrayList<BasicBlock> dominanceFrontier) {
        this.dominanceFrontier = new ArrayList<>(dominanceFrontier);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getImdomDepth() {
        return imdomDepth;
    }

    public void setImdomDepth(int imdomDepth) {
        this.imdomDepth = imdomDepth;
    }

    public void deleteForPhi(BasicBlock block) {
        for (Use use : getUseList()) {
            User user = use.getUser();
            if (user instanceof PhiInst phiInst && phiInst.getBasicBlock().equals(block)) {
                for (int i = 0; i < phiInst.getBlocks().size(); i++) {
                    if (phiInst.getBlocks().get(i).equals(this)) {
                        phiInst.getBlocks().remove(i);
                        phiInst.getOperands().remove(i);
                        i--;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return getName() + ":\n\t" +
                instructions.stream().map(Object::toString)
                        .collect(Collectors.joining("\n\t"));
    }
}
