package llvm.values.instructions;

import llvm.types.IntegerType;
import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class ZextInst extends Instruction {

    public ZextInst(Operator op, Value value, BasicBlock basicBlock) {
        super(VoidType.voidType, op, basicBlock);
        setType(IntegerType.i32);
        addOperand(value);
        setName("%" + valueIdCount);
        valueIdCount++;
    }

    public Value getValue() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return getName() + " = zext " + getValue().getType()
                + " " + getValue().getName() + " to " + getType();
    }

}
