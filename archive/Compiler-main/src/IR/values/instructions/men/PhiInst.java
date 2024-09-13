package IR.values.instructions.men;

import IR.types.IntegerType;
import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Instruction;
import IR.values.instructions.Operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PhiInst extends MemInst {
    private final ArrayList<BasicBlock> bbs = new ArrayList<>();

    public PhiInst() {
        super(IntegerType.i32, Operator.Phi);
        setName("%" + valNumber++);
    }

    public void addValue(BasicBlock basicBlock, Value value) {
        bbs.add(basicBlock);
        addOperand(value);
    }

    public ArrayList<BasicBlock> getBBs() {
        return bbs;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder(getName() + " = phi " + getType());
        for (int i = 0; i < getOperands().size(); i++) {
            ans.append(" [ ");
            ans.append(getOperands().get(i).getName()).append(", %");
            ans.append(bbs.get(i).getName());
            ans.append(" ],");
        }
        ans.deleteCharAt(ans.length() - 1);
        return ans.toString();
    }
}
