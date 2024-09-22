package middle.component;

import middle.component.model.User;
import middle.component.type.LabelType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Function extends User {
    private final ValueType returnType;
    private final ArrayList<FuncParam> funcParams = new ArrayList<>();
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    public Function(String name, ValueType returnType) {
        super(name, new LabelType());
        this.returnType = returnType;
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
}
