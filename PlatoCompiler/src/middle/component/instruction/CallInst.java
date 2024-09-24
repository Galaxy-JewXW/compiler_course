package middle.component.instruction;

import middle.IRData;
import middle.component.Function;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CallInst extends Instruction implements Call {
    public CallInst(Function calledFunction,
                    ArrayList<Value> parameters) {
        super("", calledFunction.getReturnType(), OperatorType.CALL);
        addOperands(calledFunction);
        for (Value param : parameters) {
            addOperands(param);
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

    @Override
    public boolean hasSideEffect() {
        return !getValueType().equals(IntegerType.VOID);
    }

    @Override
    public String toString() {
        String paramInfo = getParameters().stream()
                .map(param -> param.getValueType() + " " + param.getName())
                .collect(Collectors.joining(", "));
        String functionCall = String.format("%s(%s)", getCalledFunction().getName(), paramInfo);
        return getValueType().equals(IntegerType.VOID)
                ? String.format("call void %s", functionCall)
                : String.format("%s = call %s %s", getName(), getValueType(), functionCall);
    }
}
