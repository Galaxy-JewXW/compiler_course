package middle.component;

import backend.enums.Register;
import middle.IRData;
import middle.component.model.User;
import middle.component.model.Value;
import middle.component.type.LabelType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Function extends User {
    private final ValueType returnType;
    private final ArrayList<FuncParam> funcParams = new ArrayList<>();
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private boolean hasSideEffect = false;
    private boolean isBuiltIn = false;

    // 寄存器分配
    private HashMap<Value, Register> var2reg;

    public Function(String name, ValueType returnType) {
        super(name, new LabelType());
        this.returnType = returnType;
        Module.getInstance().addFunction(this);
    }

    public Function(String name, ValueType returnType, boolean isBuiltIn) {
        super(name, new LabelType());
        this.returnType = returnType;
        this.isBuiltIn = isBuiltIn;
    }

    public ValueType getReturnType() {
        return returnType;
    }

    public void addFuncParam(FuncParam funcParam) {
        funcParams.add(funcParam);
    }

    public ArrayList<FuncParam> getFuncParams() {
        return funcParams;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void updateId() {
        IRData.reset();
        for (FuncParam funcParam : funcParams) {
            funcParam.updateId();
        }
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.updateId();
        }
    }

    public boolean hasSideEffect() {
        return hasSideEffect;
    }

    public void setHasSideEffect(boolean hasSideEffect) {
        this.hasSideEffect = hasSideEffect;
    }

    public HashMap<Value, Register> getVar2reg() {
        return var2reg;
    }

    public void setVar2reg(HashMap<Value, Register> var2reg) {
        this.var2reg = var2reg;
    }

    @Override
    public String toString() {
        String paramInfo = funcParams.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        return "define dso_local " + returnType + " " +
                getName() + "(" + paramInfo + ") {\n" +
                basicBlocks.stream().map(Object::toString)
                        .collect(Collectors.joining("\n")) +
                "\n}";
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }
}
