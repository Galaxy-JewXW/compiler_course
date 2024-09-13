package IR.values.instructions.men;

import IR.types.ArrayType;
import IR.types.IntegerType;
import IR.types.PointerType;
import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

import java.util.ArrayList;

public class GEPInst extends MemInst {
    private final Type target;

    public GEPInst(Value pointer, ArrayList<Value> indices, BasicBlock basicBlock) {
        super(new PointerType(myType(pointer, indices.size())), Operator.GEP, basicBlock);
        setName("%" + valNumber++);
        addOperand(pointer);
        this.target = ((PointerType) pointer.getType()).getTargetType();
        for (Value value : indices) {
            addOperand(value);
        }
    }

    private static Type myType(Value pointer, int dim) {
        Type ans = ((PointerType) pointer.getType()).getTargetType();
        if (ans instanceof ArrayType) {
            for (int i = 1; i < dim; i++) {
                ans = ((ArrayType) ans).getElementType();
            }
        }
        return ans;
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    private String getIndex(Value value) {
        return value.getType().toString() + ' ' + value.getName();
    }

    public ArrayList<Value> getIndices() {
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
