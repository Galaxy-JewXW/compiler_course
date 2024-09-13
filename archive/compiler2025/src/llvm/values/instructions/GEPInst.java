package llvm.values.instructions;

import llvm.types.ArrayType;
import llvm.types.IntegerType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Value;

import java.util.ArrayList;

public class GEPInst extends MemInst {
    private final Type target;

    private static Type getType(Value pointer, int dimension) {
        Type tempType = ((PointerType) pointer.getType()).getTargetType();
        if (tempType instanceof ArrayType) {
            for (int i = 1; i < dimension; i++) {
                tempType = ((ArrayType) tempType).getElementType();
            }
        }
        return tempType;
    }

    public GEPInst(Value pointer, ArrayList<Value> indexes, BasicBlock basicBlock) {
        super(new PointerType(getType(pointer, indexes.size())), Operator.GEP, basicBlock);
        addOperand(pointer);
        this.target = ((PointerType) pointer.getType()).getTargetType();
        for (Value value : indexes) {
            addOperand(value);
        }
        setName("%" + valueIdCount);
        valueIdCount++;
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    private String getIndex(Value value) {
        return value.getType().toString() + ' ' + value.getName();
    }

    public ArrayList<Value> getIndexes() {
        return new ArrayList<>(getOperands().subList(1, getOperands().size()));
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(getName());
        res.append(" = getelementptr ");
        if (target instanceof ArrayType arrayType
                && arrayType.getElementType() == IntegerType.i8) {
            res.append("inbounds ");
        }
        res.append(target).append(", ");
        res.append(getPointer().getType()).append(" ");
        res.append(getPointer().getName()).append(", ");
        res.append(getIndex(getOperands().get(1)));
        for (int i = 2; i < getOperands().size(); i++) {
            res.append(", ").append(getIndex(getOperands().get(i)));
        }
        return res.toString();
    }
}
