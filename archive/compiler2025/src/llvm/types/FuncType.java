package llvm.types;

import java.util.ArrayList;

public class FuncType implements Type {
    private Type returnType;
    private final ArrayList<Type> paramsType;

    public FuncType(Type returnType, ArrayList<Type> paramsType) {
        this.returnType = returnType;
        this.paramsType = paramsType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public ArrayList<Type> getParamsType() {
        return paramsType;
    }
}
