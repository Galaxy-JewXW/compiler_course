package middle.component.model;

import middle.component.types.ValueType;

import java.util.ArrayList;

public class Value {
    private static int valueIdCount = 0;
    private String name;
    private ValueType valueType;
    private final ArrayList<Use> uses = new ArrayList<>();

    public Value(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    public static void resetIdCount() {
        valueIdCount = 0;
    }

    public static int allocIdCount() {
        return valueIdCount++;
    }

    public String getName() {
        return name;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public void addUse(Use use) {
        uses.add(use);
    }

    @Override
    public String toString() {
        return valueType.toString() + " " + name;
    }
}