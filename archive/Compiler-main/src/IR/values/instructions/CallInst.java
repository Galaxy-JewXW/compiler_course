package IR.values.instructions;

import IR.types.FuncType;
import IR.types.VoidType;
import IR.values.Assignable;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.Value;

import java.util.ArrayList;

public class CallInst extends Instruction implements Assignable {
    public CallInst(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        super(((FuncType) function.getType()).getRetType(), Operator.Call, basicBlock);
        if (getType() != VoidType.voidType) {
            setName("%" + valNumber++);
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
        StringBuilder funcName = new StringBuilder("@" + getOperands().get(0).getName() + "(");
        if (getOperands().size() > 1) {
            Value arg1 = getOperands().get(1);
            funcName.append(arg1.getType().toString()).append(' ').append(arg1.getName());
            for (int i = 2; i < getOperands().size(); i++) {
                Value argI = getOperands().get(i);
                funcName.append(",").append(argI.getType()).append(' ').append(argI.getName());
            }
        }
        funcName.append(")");
        return funcName.toString();
    }

    @Override
    public String toString() {
        if (getType() == VoidType.voidType) {
            return String.format("call void %s", getCallee());
        } else {
            return String.format("%s = call %s %s ", getName(), getType(), getCallee());
        }
    }
}
