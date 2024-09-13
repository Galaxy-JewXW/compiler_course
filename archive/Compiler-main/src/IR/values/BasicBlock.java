package IR.values;

import IO.OutputHandler;
import IR.BuildFactory;
import IR.types.LabelType;
import IR.values.instructions.Instruction;
import IR.values.instructions.terminator.TerminatorInst;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private boolean isTerminated = false;
    /* optimize */
    private final ArrayList<BasicBlock> prev = new ArrayList<>();
    private final ArrayList<BasicBlock> next = new ArrayList<>();
    private HashSet<BasicBlock> dom = new HashSet<>();
    private BasicBlock idom;//被直接支配
    private final ArrayList<BasicBlock> immDomList = new ArrayList<>();//直接支配
    private final HashSet<BasicBlock> DF = new HashSet<>();

    public BasicBlock(Function function) {
        super(String.valueOf(valNumber++), new LabelType());
        function.addBasicBlock(this);
    }

    public BasicBlock() {
        super("", null);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void giveName() {
        setName("mid" + valNumber++);
    }

    public void refill(Function function) {
        setName(String.valueOf(valNumber++));
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
        setName(String.valueOf(valNumber++));
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

    public HashSet<BasicBlock> getDom() {
        return dom;
    }

    public void setDom(HashSet<BasicBlock> dom) {
        this.dom = dom;
    }

    public void setIdom(BasicBlock idom) {
        this.idom = idom;
        idom.addImmDom(this);
    }

    public BasicBlock getIdom() {
        return idom;
    }

    public void addImmDom(BasicBlock immDom) {
        this.immDomList.add(immDom);
    }

    public ArrayList<BasicBlock> getImmDomList() {
        return immDomList;
    }

    public ArrayList<BasicBlock> getNext() {
        return next;
    }

    public ArrayList<BasicBlock> getPrev() {
        return prev;
    }

    public void addDF(BasicBlock basicBlock) {
        this.DF.add(basicBlock);
    }

    public HashSet<BasicBlock> getDF() {
        return DF;
    }

    public Instruction getFirstInstr() {
        return instructions.get(0);
    }

    public Instruction getLastInst() {
        return instructions.get(instructions.size() - 1);
    }

    public void genLLVM() {
        OutputHandler.genLLVM(getName() + ":");
        BuildFactory.getInstance().buildRetInst(this);
        for (Instruction instruction : instructions) {
            OutputHandler.genLLVM("\t" + instruction);
        }
    }
}
