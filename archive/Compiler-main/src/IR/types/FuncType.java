package IR.types;

import java.util.ArrayList;

public class FuncType implements Type {
    private Type retType;
    private ArrayList<Type> paramsType;

    public FuncType(Type retType, ArrayList<Type> paramsType) {
        this.retType = retType;
        this.paramsType = paramsType;
    }

    public Type getRetType() {
        return retType;
    }

    public ArrayList<Type> getParamsType() {
        return paramsType;
    }
}
