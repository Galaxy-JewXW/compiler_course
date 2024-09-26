package middle.component;

import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;

public class ConstString extends Value {
    private final String content;

    public ConstString(String name, String content) {
        super(name, new PointerType(new ArrayType(content.length() + 1,
                IntegerType.i8)));
        this.content = content.replace("\n", "\\0A");
        Module.getInstance().addConstString(this);
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getName() + " = private unnamed_addr constant "
                + ((PointerType) getValueType()).getTargetType()
                + " c\"" + content + "\\00\"" + ", align 1";
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ConstString) {
            return content.equals(((ConstString) obj).content);
        }
        return false;
    }
}
