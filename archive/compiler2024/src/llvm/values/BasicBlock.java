package llvm.values;

import llvm.types.LabelType;
import llvm.values.instructions.Instruction;
import llvm.values.instructions.TerminatorInstruction;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private boolean isTerminated = false;
    private final ArrayList<BasicBlock> prev = new ArrayList<>();
    private final ArrayList<BasicBlock> next = new ArrayList<>();

    public BasicBlock(Function function) {
        super(Integer.toString(valueCnt), new LabelType());
        function.addBasicBlock(this);
        valueCnt++;
    }

    public BasicBlock() {
        super("", null);
    }

    public void fillIntoFunction(Function function) {
        setName(Integer.toString(valueCnt));
        valueCnt++;
        setType(new LabelType());
        function.addBasicBlock(this);
    }

    public void addInstruction(Instruction instruction) {
        if (isTerminated) {
            return;
        }
        instructions.add(instruction);
        if (instruction instanceof TerminatorInstruction) {
            this.isTerminated = true;
        }
    }

    public boolean notTerminated() {
        return !isTerminated;
    }

    public void addPrevBlock(BasicBlock basicBlock) {
        prev.add(basicBlock);
    }

    public void addNextBlock(BasicBlock basicBlock) {
        next.add(basicBlock);
    }

    public ArrayList<BasicBlock> getPrev() {
        return prev;
    }

    public ArrayList<BasicBlock> getNext() {
        return next;
    }

    public void toLLVM() {
        System.out.println(getName() + ":");
        Builder.buildRetInstruction(this);
        for (Instruction instruction : instructions) {
            System.out.println("\t" + instruction.toString());
        }
    }
}
