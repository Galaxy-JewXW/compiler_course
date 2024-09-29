package middle.component.instruction;

import backend.enums.Register;
import middle.IRData;
import middle.component.Function;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CallInst extends Instruction implements Call {
    private HashSet<Register> activeReg = new HashSet<>();

    public CallInst(Function calledFunction,
                    ArrayList<Value> parameters) {
        super("", calledFunction.getReturnType(), OperatorType.CALL);
        addOperand(calledFunction);
        for (Value param : parameters) {
            addOperand(param);
        }
        if (!getValueType().equals(IntegerType.VOID)) {
            setName(IRData.getVarName());
        }
    }

    @Override
    public Function getCalledFunction() {
        return (Function) getOperands().get(0);
    }

    public ArrayList<Value> getParameters() {
        return new ArrayList<>(getOperands().subList(1, getOperands().size()));
    }

    public HashSet<Register> getActiveReg() {
        return activeReg;
    }

    public void setActiveReg(HashSet<Register> activeReg) {
        this.activeReg = activeReg;
    }

    @Override
    public boolean hasSideEffect() {
        return !getValueType().equals(IntegerType.VOID);
    }

    @Override
    public String getCallee() {
        String paramInfo = getParameters().stream()
                .map(param -> param.getValueType() + " " + param.getName())
                .collect(Collectors.joining(", "));
        return String.format("%s(%s)", getCalledFunction().getName(), paramInfo);
    }

    @Override
    public String toString() {
        String functionCall = getCallee();
        return getValueType().equals(IntegerType.VOID)
                ? String.format("call void %s", functionCall)
                : String.format("%s = call %s %s", getName(), getValueType(), functionCall);
    }
}
