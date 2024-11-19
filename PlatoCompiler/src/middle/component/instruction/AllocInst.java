package middle.component.instruction;

import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class AllocInst extends Instruction {
    // alloc指令在栈上分配一个地址空间，返回值是一个指针
    private final ValueType targetType;
    // 对于使用alloca定义的数组变量，统计使用的gep指令
    // 如果存在不属于该数组的gep指令，可以认为该数组变量不属于无用变量
    private final ArrayList<GepInst> gepInsts = new ArrayList<>();
    private final ArrayList<StoreInst> storeInsts = new ArrayList<>();

    public AllocInst(ValueType targetType) {
        super(new PointerType(targetType), OperatorType.ALLOC);
        this.targetType = targetType;
    }

    public void addGepInst(GepInst gepInst) {
        gepInsts.add(gepInst);
    }

    public void addStoreInst(StoreInst storeInst) {
        storeInsts.add(storeInst);
    }

    public ArrayList<GepInst> getGepInsts() {
        return gepInsts;
    }

    public ArrayList<StoreInst> getStoreInsts() {
        return storeInsts;
    }

    public ValueType getTargetType() {
        return targetType;
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + targetType;
    }
}
