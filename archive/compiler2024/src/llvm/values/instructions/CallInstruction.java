package llvm.values.instructions;

import llvm.types.FunctionType;
import llvm.types.VoidType;
import llvm.values.Assignable;
import llvm.values.BasicBlock;
import llvm.values.Function;
import llvm.values.Value;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CallInstruction extends Instruction implements Assignable {

    public CallInstruction(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        super(((FunctionType) function.getType()).getReturnType(), Operator.CALL, basicBlock);
        if (!getType().equals(new VoidType())) {
            setName("%" + valueCnt);
            valueCnt++;
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
        StringJoiner sj = new StringJoiner(",", "@" + getOperands().get(0).getName() + "(", ")");
        ArrayList<Value> operands = getOperands();
        for (int i = 1; i < operands.size(); i++) {
            Value arg = operands.get(i);
            sj.add(arg.getType() + " " + arg.getName());
        }
        return sj.toString();
    }


    @Override
    public String toString() {
        if (getType().equals(new VoidType())) {
            return String.format("call void %s", getCallee());
        } else {
            return String.format("%s = call %s %s ", getName(), getType(), getCallee());
        }
    }
}
