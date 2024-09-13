package IR.values.instructions;

import IR.types.IntegerType;
import IR.types.VoidType;
import IR.values.BasicBlock;
import IR.values.Value;

public class ConvInst extends Instruction {

    public ConvInst(Operator op, Value value, BasicBlock basicBlock) {
        super(VoidType.voidType, op, basicBlock);
        setName("%" + valNumber++);
        if (op == Operator.Zext) {
            setType(IntegerType.i32);
        }
        addOperand(value);
    }

    public Value getValue() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        if (getOp() == Operator.Zext) {
            return String.format("%s = zext %s %s to %s",
                    getName(), getValue().getType(), getValue().getName(), getType());
        }
        return null;
    }
}
