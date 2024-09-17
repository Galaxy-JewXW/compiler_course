package middle;

import middle.instructions.Instruction;
import middle.instructions.TerminatorInst;
import middle.model.Value;
import middle.types.LabelType;
import tools.InstBuilder;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private boolean isTerminated = false;

    public BasicBlock(Function function) {
        super(Integer.toString(valueIdCount++), new LabelType());
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

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void refill(Function function) {
        setName(Integer.toString(valueIdCount));
        valueIdCount++;
        setValueType(new LabelType());
        function.addBasicBlock(this);
    }

    public void toLLVM() {
        System.out.println(getName() + ":");
        InstBuilder.buildRetInst(this);
        for (Instruction instruction : instructions) {
            System.out.println("\t" + instruction);
        }
    }
}
