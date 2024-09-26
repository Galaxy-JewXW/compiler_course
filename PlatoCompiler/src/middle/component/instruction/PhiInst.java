package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashSet;

public class PhiInst extends Instruction {
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();
    private final HashSet<BasicBlock> gotBlocks = new HashSet<>();

    public PhiInst(ValueType valueType) {
        super(valueType, OperatorType.PHI);
    }

    public PhiInst(ValueType valueType, BasicBlock block) {
        super(valueType, OperatorType.PHI, block);
    }


    public void addValue(BasicBlock block, Value value) {
        if (!gotBlocks.contains(block)) {
            gotBlocks.add(block);
            blocks.add(block);
            addOperand(value);
        }
    }

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName() + " = phi " + getValueType());
        for (int i = 0; i < getOperands().size(); i++) {
            sb.append(" [ ");
            sb.append(getOperands().get(i).getName()).append(", %");
            sb.append(blocks.get(i).getName());
            sb.append(" ],");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
