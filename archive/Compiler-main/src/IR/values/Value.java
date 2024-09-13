package IR.values;


import IR.types.IntegerType;
import IR.types.Type;

import java.util.ArrayList;

public class Value {
    private String name;
    private Type type;
    private final ArrayList<Use> useList = new ArrayList<>();
    public static int valNumber = 0;

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void addUse(Use use) {
        this.useList.add(use);
    }

    public ArrayList<Use> getUseList() {
        return useList;
    }

    public void replacedByNewVal(Value newVal) {
        for (Use use : useList) {
            use.getUser().replaceVal(use.getPos(), newVal);
            use.setValue(newVal);
            newVal.addUse(use);
        }
    }

    public void deleteUser(User user) {
        useList.removeIf(use -> use.getUser() == user);
    }

    public boolean unUsed() {
        return useList.isEmpty();
    }

    public static Value newTmpValue(String name) {
        return new Value("tmp_" + name, IntegerType.i32);
    }

    @Override
    public String toString() {
        return type.toString() + ' ' + name;
    }
}
