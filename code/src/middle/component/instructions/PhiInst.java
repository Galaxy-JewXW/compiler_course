package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.types.ValueType;

import java.util.ArrayList;

public class PhiInst extends MemInst {
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();

    public PhiInst(ValueType valueType) {
        super(valueType, OperatorType.PHI);
        setName("%" + allocIdCount());
    }

    public void addBlock(BasicBlock block) {
        blocks.add(block);
        addOperand(block);
    }

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
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
