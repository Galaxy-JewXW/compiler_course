package llvm.values.instructions;

import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class RetInstruction extends TerminatorInstruction {
    public RetInstruction(BasicBlock basicBlock) {
        super(new VoidType(), Operator.RET, basicBlock);
    }

    public RetInstruction(BasicBlock basicBlock, Value value) {
        super(value.getType(), Operator.RET, basicBlock);
        addOperand(value);
    }

    @Override
    public String toString() {
        if (getType().equals(new VoidType())) {
            return "ret void";
        } else {
            return "ret" + " " + getOperands().get(0).getType()
                    + " " + getOperands().get(0).getName();
        }
    }
}
