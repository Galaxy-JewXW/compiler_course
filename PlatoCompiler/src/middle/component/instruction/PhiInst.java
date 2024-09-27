package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class PhiInst extends Instruction {
    private final ArrayList<BasicBlock> blocks;

    public PhiInst(ValueType type, BasicBlock block, ArrayList<BasicBlock> blocks) {
        super(type, OperatorType.PHI);
        setBasicBlock(block);
        this.blocks = new ArrayList<>(blocks);
        for (int i = 0; i < blocks.size(); i++) {
            getOperands().add(null);
            blocks.get(i).addUse(this);
        }
    }

    public void addValue(BasicBlock block, Value value) {
        int index = blocks.indexOf(block);
        getOperands().set(index, value);
        value.addUse(this);
    }

    @Override
    public void modifyOperand(Value value, Value newValue) {
        if (value instanceof BasicBlock) {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).equals(value)) {
                    blocks.set(i, (BasicBlock) newValue);
                }
            }
        } else {
            for (int i = 0; i < getOperands().size(); i++) {
                if (getOperands().get(i).equals(value)) {
                    getOperands().set(i, newValue);
                    newValue.addUse(this);
                }
            }
        }
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getName())
                .append(" = phi ")
                .append(getValueType())
                .append(" ");

        for (int i = 0; i < blocks.size(); i++) {
            sb.append(i == 0 ? "[ " : ",[ ")
                    .append(getOperands().get(i).getName())
                    .append(", %")
                    .append(blocks.get(i).getName())
                    .append(" ]");
        }

        return sb.toString();
    }
}
