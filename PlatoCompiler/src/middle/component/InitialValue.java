package middle.component;

import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class InitialValue {
    private final ValueType valueType;
    private final int length;
    private final ArrayList<Integer> elements;

    public InitialValue(ValueType valueType, int length, ArrayList<Integer> elements) {
        this.valueType = valueType;
        this.length = length;
        this.elements = elements;
    }

    public ArrayList<Integer> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        boolean flag = valueType.equals(IntegerType.i8)
                || valueType.equals(IntegerType.i32);
        if (elements == null) {
            if (flag) {
                return valueType + " " + 0;
            } else {
                return valueType + " zeroinitializer";
            }
        } else {
            if (flag) {
                return valueType + " " + elements.get(0);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(valueType).append(" [");
                int filled = elements.size();
                for (Integer element : elements) {
                    sb.append(((ArrayType) valueType).getElementType())
                            .append(" ").append(element.toString()).append(", ");
                }
                if (filled == length) {
                    sb.delete(sb.length() - 2, sb.length());
                } else {
                    sb.append("zeroinitializer");
                }
                sb.append("]");
                return sb.toString();
            }
        }
    }
}
