package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class PhiInst extends Instruction {
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();

    public PhiInst(ValueType valueType) {
        super(valueType, OperatorType.PHI);
    }

    public void addValue(BasicBlock block, Value value) {
        blocks.add(block);
        addOperand(value);
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
