package middle.instructions;

import middle.BasicBlock;
import middle.model.Value;
import middle.types.VoidType;

public class RetInst extends TerminatorInst {
    public RetInst(BasicBlock basicBlock) {
        super(VoidType.VOID, OperatorType.RET, basicBlock);
    }

    public RetInst(BasicBlock basicBlock, Value returnValue) {
        super(returnValue.getValueType(), OperatorType.RET, basicBlock);
        addOperand(returnValue);
    }

    @Override
    public String toString() {
        if (getValueType() == VoidType.VOID) {
            return "ret void";
        } else {
            return "ret" + " " + getOperands().get(0).getValueType()
                    + " " + getOperands().get(0).getName();
        }
    }
}
