package middle.component;

import middle.IRData;
import middle.component.instruction.Instruction;
import middle.component.model.Value;
import middle.component.type.LabelType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private Function function;

    // 前驱基本块的集合
    private final ArrayList<BasicBlock> prevBlocks = new ArrayList<>();
    // 后继基本块的集合
    private final ArrayList<BasicBlock> nextBlocks = new ArrayList<>();


    public BasicBlock(String name) {
        super(name, new LabelType());
        this.function = IRData.getCurrentFunction();
        function.addBasicBlock(this);
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

    public void addPrevBlock(BasicBlock prevBlock) {
        prevBlocks.add(prevBlock);
    }

    public void addNextBlock(BasicBlock nextBlock) {
        nextBlocks.add(nextBlock);
    }

    @Override
    public String toString() {
        return getName() + ":\n\t" +
                instructions.stream().map(Object::toString)
                .collect(Collectors.joining("\n\t"));
    }
}
