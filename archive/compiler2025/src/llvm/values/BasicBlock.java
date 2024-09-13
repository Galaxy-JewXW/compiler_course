package llvm.values;

import llvm.InstrBuilder;
import llvm.types.LabelType;
import llvm.values.instructions.Instruction;
import llvm.values.instructions.TerminatorInst;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private boolean isTerminated = false;
    private final ArrayList<BasicBlock> prev = new ArrayList<>();
    private final ArrayList<BasicBlock> next = new ArrayList<>();

    public BasicBlock(Function function) {
        super(Integer.toString(valueIdCount), new LabelType());
        valueIdCount++;
        function.addBasicBlock(this);
    }

    public BasicBlock() {
        super("", null);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void giveName() {
        setName("mid" + valueIdCount);
        valueIdCount++;
    }

    public void refill(Function function) {
        setName(Integer.toString(valueIdCount));
        valueIdCount++;
        setType(new LabelType());
        function.addBasicBlock(this);
    }

    public void addInstruction(Instruction instruction) {
        if (isTerminated) {
            return;
        }
        instructions.add(instruction);
        if (instruction instanceof TerminatorInst) {
            this.isTerminated = true;
        }
    }

    /* optimize */
    public void refreshName() {
        setName(String.valueOf(valueIdCount++));
        for (Instruction instruction : instructions) {
            instruction.refreshName();
        }
    }

    public void addPrevBlock(BasicBlock basicBlock) {
        this.prev.add(basicBlock);
    }

    public void addNextBlock(BasicBlock basicBlock) {
        this.next.add(basicBlock);
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public ArrayList<BasicBlock> getNext() {
        return next;
    }

    public ArrayList<BasicBlock> getPrev() {
        return prev;
    }

    public Instruction getFirstInstr() {
        return instructions.get(0);
    }

    public Instruction getLastInst() {
        return instructions.get(instructions.size() - 1);
    }

    public void toLLVM() {
        System.out.println(getName() + ":");
        InstrBuilder.buildRetInst(this);
        for (Instruction instruction : instructions) {
            System.out.println("\t" + instruction);
        }
    }
}
