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
        this.content = content;
    }

    @Override
    public String toString() {
        return getName() + " = constant " + ((PointerType) getValueType()).getTargetType()
                + " c\"" + content + "\\00\"";
    }
}
