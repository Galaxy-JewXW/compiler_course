package llvm.values.instructions;

import llvm.types.IntegerType;
import llvm.values.BasicBlock;
import llvm.values.Value;

import java.util.ArrayList;

public class PhiInst extends MemInst {
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    public PhiInst() {
        super(IntegerType.i32, Operator.PHI);
        setName("%" + valueIdCount);
        valueIdCount++;
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
        StringBuilder ans = new StringBuilder(getName() + " = phi " + getType());
        for (int i = 0; i < getOperands().size(); i++) {
            ans.append(" [ ");
            ans.append(getOperands().get(i).getName()).append(", %");
            ans.append(basicBlocks.get(i).getName()).append(" ],");
        }
        ans.deleteCharAt(ans.length() - 1);
        return ans.toString();
    }
}
