package middle.component;

import middle.IRData;
import middle.component.instruction.Instruction;
import middle.component.model.Value;
import middle.component.type.LabelType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    // 自身直接支配的基本块，区分 支配 和 直接支配
    // 或支配树的子节点
    private final HashSet<BasicBlock> immediateDominatedBlocks = new HashSet<>();
    // 支配边界
    private final HashSet<BasicBlock> dominanceFrontier = new HashSet<>();
    // 前驱基本块的集合
    private ArrayList<BasicBlock> prevBlocks = new ArrayList<>();
    // 后继基本块的集合
    private ArrayList<BasicBlock> nextBlocks = new ArrayList<>();
    private Function function;
    // 可支配的基本块的集合
    private HashSet<BasicBlock> dominatedBlocks = null;
    // 被直接支配的基本块，或支配树的根节点
    private BasicBlock immediateDominator = null;
    // 标记是否在优化过程中被删除
    private boolean isDeleted = false;


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

    public ArrayList<BasicBlock> getPrevBlocks() {
        return prevBlocks;
    }

    public ArrayList<BasicBlock> getNextBlocks() {
        return nextBlocks;
    }

    public void setNextBlocks(ArrayList<BasicBlock> nextBlocks) {
        this.nextBlocks = new ArrayList<>(nextBlocks);
    }

    public void addPrevBlock(BasicBlock prevBlock) {
        prevBlocks.add(prevBlock);
    }

    public void addNextBlock(BasicBlock nextBlock) {
        nextBlocks.add(nextBlock);
    }

    public HashSet<BasicBlock> getDominatedBlocks() {
        return dominatedBlocks;
    }

    public void setDominatedBlocks(HashSet<BasicBlock> dominatedBlocks) {
        this.dominatedBlocks = dominatedBlocks;
    }

    public boolean dominant(BasicBlock block) {
        if (dominatedBlocks == null) {
            throw new IllegalStateException("dominatedBlocks not set");
        }
        return dominatedBlocks.contains(block);
    }

    public boolean strictDominant(BasicBlock block) {
        if (dominatedBlocks == null) {
            throw new IllegalStateException("dominatedBlocks not set");
        }
        return dominatedBlocks.contains(block) && !block.equals(this);
    }

    public BasicBlock getImmediateDominator() {
        return immediateDominator;
    }

    public void setImmediateDominator(BasicBlock immediateDominator) {
        this.immediateDominator = immediateDominator;
        immediateDominator.immediateDominatedBlocks.add(this);
    }

    public HashSet<BasicBlock> getImmediateDominatedBlocks() {
        return immediateDominatedBlocks;
    }

    public void addDominantFrontier(BasicBlock frontier) {
        dominanceFrontier.add(frontier);
    }

    public HashSet<BasicBlock> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return getName() + ":\n\t" +
                instructions.stream().map(Object::toString)
                        .collect(Collectors.joining("\n\t"));
    }
}
