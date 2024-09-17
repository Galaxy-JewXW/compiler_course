package middle.instructions;

import middle.BasicBlock;
import middle.Function;
import middle.model.Value;
import middle.types.Assignable;
import middle.types.FunctionType;
import middle.types.VoidType;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CallInst extends Instruction implements Assignable {
    public CallInst(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        super(((FunctionType) function.getValueType()).getReturnType(), OperatorType.CALL, basicBlock);
        if (getValueType() != VoidType.VOID) {
            setName("%" + valueIdCount++);
        }
        addOperand(function);
        for (Value arg : args) {
            addOperand(arg);
        }
    }

    public String getCallee() {
        StringJoiner sj = new StringJoiner(", ", "@" + getOperands().get(0).getName() + "(", ")");
        ArrayList<Value> operands = getOperands();
        for (int i = 1; i < operands.size(); i++) {
            Value arg = operands.get(i);
            sj.add(arg.getValueType() + " " + arg.getName());
        }
        return sj.toString();
    }

    @Override
    public String toString() {
        if (getValueType() == VoidType.VOID) {
            return "call void " + getCallee();
        } else {
            return getName() + " = call " + getValueType() + " " + getCallee();
        }
    }
}
