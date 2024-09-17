package middle.types;

import java.util.ArrayList;

public class FunctionType extends ValueType {
    private final ValueType returnType;
    private final ArrayList<ValueType> parametersTypes;

    public FunctionType(ValueType returnType, ArrayList<ValueType> parametersTypes) {
        this.returnType = returnType;
        this.parametersTypes = parametersTypes;
    }

    public ValueType getReturnType() {
        return returnType;
    }

    public ArrayList<ValueType> getParametersTypes() {
        return parametersTypes;
    }
}
