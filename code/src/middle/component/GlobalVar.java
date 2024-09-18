package middle.component;

import middle.Module;
import middle.component.model.User;
import middle.component.model.Value;
import middle.component.types.PointerType;
import middle.component.types.ValueType;

/* 全局变量类，定义在bss上，为全局且可变/不可变的变量。
 * 变量是否可变取决于isConstant属性
 * 打印格式形如 @a = dso_local global(constant) i32 3
 * 这里a也是指针，使用之前也需要使用load
 */
public class GlobalVar extends User {
    private final Value value;
    private final boolean isConstant;

    public GlobalVar(String name, ValueType valueType, Value value, boolean isConstant) {
        super("@" + name, new PointerType(valueType));
        this.value = value;
        this.isConstant = isConstant;
        Module.getInstance().addGlobalVar(this);
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getName() + " = dso_local " +
                (isConstant ? "constant " : "global ") +
                value.toString();
    }
}
