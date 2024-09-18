package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;
import middle.types.ArrayType;
import middle.types.PointerType;
import middle.types.ValueType;

import java.util.ArrayList;

public class GepInst extends MemInst {
    private final ValueType targetType;

    private static ValueType preWork(Value pointer) {
        ValueType type = ((PointerType) pointer.getValueType()).getTargetType();
        type = ((ArrayType) type).getElementType();
        return type;
    }

    public GepInst(Value pointer, ArrayList<Value> indexes, BasicBlock basicBlock) {
        super(new PointerType(preWork(pointer)), OperatorType.GEP, basicBlock);
        addOperand(pointer);
        this.targetType = ((PointerType) pointer.getValueType()).getTargetType();
        for (Value index : indexes) {
            addOperand(index);
        }
        setName("%" + valueIdCount++);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    private String getIndex(Value value) {
        return value.getValueType().toString() + ' ' + value.getName();
    }

    public String toString() {
        StringBuilder res = new StringBuilder(getName());
        res.append(" = getelementptr inbounds ");
        res.append(targetType).append(", ");
        res.append(getPointer().getValueType()).append(" ");
        res.append(getPointer().getName()).append(", ");
        res.append(getIndex(getOperands().get(1)));
        for (int i = 2; i < getOperands().size(); i++) {
            res.append(", ").append(getIndex(getOperands().get(i)));
        }
        return res.toString();
    }
}
