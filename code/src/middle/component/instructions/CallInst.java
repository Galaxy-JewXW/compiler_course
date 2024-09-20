package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.model.Value;
import middle.component.types.Assignable;
import middle.component.types.FunctionType;
import middle.component.types.VoidType;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CallInst extends Instruction implements Assignable {
    public CallInst(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        super(((FunctionType) function.getValueType()).getReturnType(), OperatorType.CALL, basicBlock);
        if (getValueType() != VoidType.VOID) {
            setName("%" + allocIdCount());
        }
        addOperand(function);
        for (Value arg : args) {
            addOperand(arg);
        }
    }

    public Function getFunction() {
        return (Function) getOperands().get(0);
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
