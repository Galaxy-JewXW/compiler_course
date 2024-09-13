package llvm.values.instructions;

import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Value;

public class RetInst extends TerminatorInst {
    public RetInst(BasicBlock basicBlock) {
        super(VoidType.voidType, Operator.RET, basicBlock);
    }

    public RetInst(BasicBlock basicBlock, Value value) {
        super(value.getType(), Operator.RET, basicBlock);
        addOperand(value);
    }

    @Override
    public String toString() {
        if (getType() == VoidType.voidType) {
            return "ret void";
        } else {
            return "ret" + " " + getOperands().get(0).getType()
                    + " " + getOperands().get(0).getName();
        }
    }
}
