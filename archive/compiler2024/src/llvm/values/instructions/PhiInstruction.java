package llvm.values.instructions;

import llvm.types.IntType;
import llvm.values.BasicBlock;
import llvm.values.Value;

import java.util.ArrayList;

public class PhiInstruction extends MemoryInstruction {
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    public PhiInstruction() {
        super(new IntType(32), Operator.PHI);
        setName("%" + valueCnt);
        valueCnt++;
    }

    public void addValue(BasicBlock basicBlock, Value value) {
        basicBlocks.add(basicBlock);
        addOperand(value);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName() + " = phi " + getType());
        for (int i = 0; i < getOperands().size(); i++) {
            sb.append(" [ ");
            sb.append(getOperands().get(i).getName()).append(", %");
            sb.append(basicBlocks.get(i).getName());
            sb.append(" ],");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}