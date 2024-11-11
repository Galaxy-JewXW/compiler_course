package middle.component;

import middle.IRData;
import middle.component.model.Value;
import middle.component.type.ValueType;

public class FuncParam extends Value {
    public FuncParam(String name, ValueType valueType) {
        super(name, valueType);
    }

    public void updateId() {
        setName(IRData.getVarName());
    }

    @Override
    public String toString() {
        return getValueType() + " " + getName();
    }
}
