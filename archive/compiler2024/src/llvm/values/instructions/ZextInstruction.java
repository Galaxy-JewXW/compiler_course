package llvm.values.instructions;

import llvm.types.IntType;
import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class ZextInstruction extends Instruction {
    public ZextInstruction(Operator op, Value value, BasicBlock basicBlock) {
        super(new VoidType(), op, basicBlock);
        setName("%" + valueCnt);
        valueCnt++;
        setType(new IntType(32));
        addOperand(value);
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
