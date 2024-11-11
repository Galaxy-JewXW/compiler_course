package middle.component.model;

import middle.component.type.ValueType;

import java.util.ArrayList;

public class Value {
    private final ValueType valueType;
    private final ArrayList<User> userList = new ArrayList<>();
    private String name;

    public Value(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void addUse(User user) {
        userList.add(user);
    }

    // 删除这个值的使用者
    // 该User不再使用此value
    public void deleteUser(User user) {
        userList.remove(user);
    }

    // 将原本使用该值的地方全部替换为新的值
    public void replaceByNewValue(Value newValue) {
        for (User user : userList) {
            user.modifyOperand(this, newValue);
        }
        userList.clear();
    }
}
