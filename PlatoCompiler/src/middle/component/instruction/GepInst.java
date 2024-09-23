package middle.component.instruction;

import middle.component.BasicBlock;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

public class GepInst extends Instruction {
    public static ValueType getType(Value pointer) {
        PointerType pointerType = (PointerType) pointer.getValueType();
        ValueType targetType = pointerType.getTargetType();
        if (targetType instanceof ArrayType arrayType) {
            return arrayType.getElementType();
        } else if (targetType instanceof IntegerType integerType) {
            return integerType;
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    public GepInst(String name, Value pointer, Value index, BasicBlock block) {
        super(name, getType(pointer), OperatorType.GEP, block);
        addOperands(pointer);
        addOperands(index);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    public Value getIndex() {
        return getOperands().get(1);
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public String toString() {
        Value pointer = getPointer();
        PointerType pointerType = (PointerType) pointer.getValueType();
        ValueType targetType = pointerType.getTargetType();

        StringBuilder sb = new StringBuilder(getName())
                .append(" = getelementptr inbounds ")
                .append(targetType)
                .append(", ")
                .append(pointerType)
                .append(" ")
                .append(pointer.getName());
        if (targetType instanceof ArrayType) {
            sb.append(", i32 0, i32 ");
        } else {
            sb.append(", i32 ");
        }
        sb.append(getIndex().getName());
        return sb.toString();
    }
}
