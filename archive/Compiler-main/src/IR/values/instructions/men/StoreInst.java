package IR.values.instructions.men;

import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

public class StoreInst extends MemInst {

    //ptr 指向被赋值地址，val是需要的赋值
    public StoreInst(BasicBlock basicBlock, Value val, Value ptr) {
        super(val.getType(), Operator.Store, basicBlock);
        addOperand(val);
        addOperand(ptr);
    }

    public Value getVal() {
        return getOperands().get(0);
    }

    public Value getPtr() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s %s",
                getVal().getType(), getVal().getName(), getPtr().getType(), getPtr().getName());
    }
}
