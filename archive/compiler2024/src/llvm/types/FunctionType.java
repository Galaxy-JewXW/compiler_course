package llvm.types;

import java.util.ArrayList;

public class FunctionType implements ValueType {
    private final ValueType returnType;
    private final ArrayList<ValueType> paramsType;

    public FunctionType(ValueType returnType, ArrayList<ValueType> paramsType) {
        this.returnType = returnType;
        this.paramsType = paramsType;
    }

    public ValueType getReturnType() {
        return returnType;
    }

    public ArrayList<ValueType> getParamsType() {
        return paramsType;
    }
}
