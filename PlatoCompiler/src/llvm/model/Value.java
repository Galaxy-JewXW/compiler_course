package llvm.model;

import llvm.type.ValueType;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Value {
    private final String name;
    private final ValueType valueType;
    private final LinkedHashSet<Use> useList = new LinkedHashSet<>();

    public Value(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public LinkedHashSet<Use> getUseList() {
        return useList;
    }

    public void addUse(User user) {
        Use use = new Use(user, this);
        useList.add(use);
    }

    // 删除这个值的使用者
    // 该User不再使用此value
    public void deleteUser(User user) {
        useList.removeIf(use -> use.getUser().equals(user));
    }

    // 将原本使用该值的地方全部替换为新的值
    public void replaceByNewValue(Value newValue) {
        Set<User> users = useList.stream()
                .map(Use::getUser)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for (User user : users) {
            user.modifyOperand(this, newValue);
        }
    }

    public void toMips() {
        // 该方法必须被重写
        throw new RuntimeException("Not implemented yet");
    }
}
