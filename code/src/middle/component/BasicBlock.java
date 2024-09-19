package middle.component;

import middle.component.instructions.Instruction;
import middle.component.instructions.TerminatorInst;
import middle.component.model.Value;
import middle.component.types.LabelType;
import tools.Builder;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    // 前驱基本块的集合
    private final ArrayList<BasicBlock> prevBlocks = new ArrayList<>();
    // 后继基本块的集合
    private final ArrayList<BasicBlock> nextBlocks = new ArrayList<>();
    // 支配的基本块的集合
    private HashSet<BasicBlock> dominantees = null;
    // 被直接支配的基本块
    private BasicBlock immediateDominator = null;
    private HashSet<BasicBlock> immediateDominants = new HashSet<>();
    private boolean isTerminated = false;

    public BasicBlock(Function function) {
        super(Integer.toString(Value.allocIdCount()), new LabelType());
        function.addBasicBlock(this);
    }

    public BasicBlock() {
        super("", null);
    }

    public void addInstruction(Instruction instruction) {
        if (isTerminated) {
            return;
        }
        instructions.add(instruction);
        if (instruction instanceof TerminatorInst) {
            isTerminated = true;
        }
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public ArrayList<BasicBlock> getPrevBlocks() {
        return prevBlocks;
    }

    public ArrayList<BasicBlock> getNextBlocks() {
        return nextBlocks;
    }

    public void addPrevBlock(BasicBlock prevBlock) {
        prevBlocks.add(prevBlock);
    }

    public void addNextBlock(BasicBlock nextBlock) {
        nextBlocks.add(nextBlock);
    }

    public void setDominantees(HashSet<BasicBlock> dominantees) {
        this.dominantees = dominantees;
    }

    public HashSet<BasicBlock> getDominantees() {
        return dominantees;
    }

    public boolean dominant(BasicBlock block) {
        if (dominantees == null) {
            throw new IllegalStateException("dominantees not set");
        }
        return dominantees.contains(block);
    }

    public boolean strictDominant(BasicBlock block) {
        if (dominantees == null) {
            throw new IllegalStateException("dominantees not set");
        }
        return dominantees.contains(block) && !block.equals(this);
    }

    public void setImmediateDominator(BasicBlock immediateDominator) {
        this.immediateDominator = immediateDominator;
        immediateDominator.immediateDominants.add(this);
    }

    // 当有新的变量引入时，需要跳转到之后还未构建的基本块，进行重填
    public void refill(Function function) {
        setName(Integer.toString(Value.allocIdCount()));
        setValueType(new LabelType());
        function.addBasicBlock(this);
    }

    public void toLLVM() {
        System.out.println(getName() + ":");
        Builder.buildRetInst(this);
        for (Instruction instruction : instructions) {
            System.out.println("\t" + instruction);
        }
    }


}
