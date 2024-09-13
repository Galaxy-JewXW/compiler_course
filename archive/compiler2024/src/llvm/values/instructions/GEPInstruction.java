package llvm.values.instructions;

import llvm.types.ArrayType;
import llvm.types.IntType;
import llvm.types.PointerType;
import llvm.types.ValueType;
import llvm.values.BasicBlock;
import llvm.values.Value;

import java.util.ArrayList;

public class GEPInstruction extends MemoryInstruction {
    private final ValueType target;

    public GEPInstruction(Value pointer, ArrayList<Value> values, BasicBlock basicBlock) {
        super(new PointerType(genType(pointer, values.size())), Operator.GETELEMENTPOINTER, basicBlock);
        setName("%" + valueCnt);
        valueCnt++;
        addOperand(pointer);
        this.target = ((PointerType) pointer.getType()).getPointToType();
        for (Value value : values) {
            addOperand(value);
        }
    }

    private static ValueType genType(Value pointer, int dim) {
        ValueType valueType = ((PointerType) pointer.getType()).getPointToType();
        if (valueType instanceof ArrayType) {
            for (int i = 1; i < dim; i++) {
                valueType = ((ArrayType) valueType).getElementType();
            }
        }
        return valueType;
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    public ArrayList<Value> getValues() {
        return new ArrayList<>(getOperands().subList(1, getOperands().size()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" = getelementptr ");
        if (target instanceof ArrayType arrayType
                && arrayType.getElementType().equals(new IntType(8))) {
            sb.append("inbounds ");
        }
        sb.append(target).append(", ");
        sb.append(getPointer().getType()).append(" ").append(getPointer().getName()).append(", ");
        sb.append(getIndex(getOperands().get(1)));
        for (int i = 2; i < getOperands().size(); i++) {
            sb.append(", ").append(getIndex(getOperands().get(i)));
        }
        return sb.toString();
    }

    private String getIndex(Value value) {
        return value.getType().toString() + " " + value.getName();
    }
}
