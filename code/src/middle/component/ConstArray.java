package middle.component;

import middle.component.model.Value;
import middle.component.types.ArrayType;
import middle.component.types.ValueType;

import java.util.ArrayList;

public class ConstArray extends ConstVar {
    // length为数组定义时所设置的长度
    private final int length;
    // filled是指定义时被填充的值的个数
    // int a[10] = {1}; 此时filled = 1
    private int filled = 0;
    private final ArrayList<Value> elements = new ArrayList<>();

    public ConstArray(int length) {
        super("", null);
        this.length = length;
    }

    public ConstArray(ValueType type, int length) {
        super("", type);
        this.length = length;
    }

    public ArrayList<Value> getElements() {
        return elements;
    }

    public void addElement(Value element) {
        elements.add(element);
    }

    public void setFilled() {
        filled = elements.size();
    }

    public void resetType() {
        setValueType(new ArrayType(elements.get(0).getValueType(), length));
    }

    public int getFilled() {
        return filled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getValueType().toString()).append(" [");
        for (int i = 0; i < filled; i++) {
            sb.append(elements.get(i).toString()).append(", ");
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
