package backend.global;

import java.util.ArrayList;

public class Word extends GlobalAssembly {
    // 一块或一系列4字节大小的空间
    private final String name;
    private final ArrayList<Integer> values;
    private final int length;
    private final boolean isArray;

    public Word(String name, ArrayList<Integer> values, int length) {
        this.name = name;
        this.values = values;
        this.isArray = true;
        this.length = length;
    }

    public Word(String name, int value) {
        this.name = name;
        this.values = new ArrayList<>();
        this.values.add(value);
        this.isArray = false;
        this.length = -1;
    }

    public String toString() {
        if (!isArray) {
            return name + ": .word " + values.get(0);
        } else if (values == null) {
            return name + ": .word 0:" + length;
        } else {
            if (!values.isEmpty()) {
                StringBuilder sb = new StringBuilder(name + ": .word ");
                for (int v : values) {
                    sb.append(v);
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                if (values.size() < length) {
                    // word部分是已经存在的定义
                    sb.append(" # existed definition\n");
                    sb.append(" ".repeat(Math.max(0, name.length() + 2)));
                    // space部分是后面未初始化的空间，使用space自动置0
                    sb.append(".space ").append(4 * (length - values.size()));
                    sb.append(" # zeroinitializer");
                }
                return sb.toString();
            } else {
                return name + ": .space " + 4 * length;
            }
        }
    }
}
