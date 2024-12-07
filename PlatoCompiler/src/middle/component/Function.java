package middle.component;

import backend.enums.Register;
import middle.IRData;
import middle.component.instruction.Call;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.io.IOInst;
import middle.component.model.User;
import middle.component.model.Value;
import middle.component.type.LabelType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.stream.Collectors;

public class Function extends User {
    private final ValueType returnType;
    private final ArrayList<FuncParam> funcParams = new ArrayList<>();
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private boolean isBuiltIn = false;
    private boolean hasSideEffects = false;

    // 寄存器分配
    private HashMap<Value, Register> var2reg = new HashMap<>();

    public Function(String name, ValueType returnType) {
        super(name, new LabelType());
        this.returnType = returnType;
        if (IRData.isInsect()) {
            Module.getInstance().addFunction(this);
        }
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

    public void setBasicBlocks(ArrayList<BasicBlock> basicBlocks) {
        this.basicBlocks.clear();
        this.basicBlocks.addAll(basicBlocks);
    }

    public BasicBlock getEntryBlock() {
        return basicBlocks.get(0);
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

    public boolean hasSideEffects() {
        return hasSideEffects;
    }

    public void setHasSideEffects(boolean hasSideEffects) {
        this.hasSideEffects = hasSideEffects;
    }

    public HashMap<Value, Register> getVar2reg() {
        return var2reg;
    }

    public void setVar2reg(HashMap<Value, Register> var2reg) {
        this.var2reg = var2reg;
    }

    public ArrayList<BasicBlock> getPostOrder() {
        ArrayList<BasicBlock> postOrder = new ArrayList<>();
        Stack<BasicBlock> stack = new Stack<>();
        HashSet<BasicBlock> visited = new HashSet<>();
        BasicBlock entry = this.getEntryBlock();
        stack.push(entry);
        while (!stack.isEmpty()) {
            BasicBlock current = stack.peek();
            // 如果当前节点未被访问过，则标记为已访问并将其子节点压入栈中
            if (!visited.contains(current)) {
                visited.add(current);
                // 逆序压入子节点，以确保按照原始顺序访问
                ArrayList<BasicBlock> children = new ArrayList<>(
                        current.getImmediateDominateBlocks());
                for (int i = children.size() - 1; i >= 0; i--) {
                    BasicBlock child = children.get(i);
                    if (!visited.contains(child)) {
                        stack.push(child);
                    }
                }
            } else {
                // 如果当前节点已被访问过，说明其所有子节点已被处理，添加到 postOrder 中
                stack.pop();
                postOrder.add(current);
            }
        }
        return postOrder;
    }

    public boolean canReplace() {
        // 用一个集合来记录已经访问过的函数
        HashSet<Function> visitedFunctions = new HashSet<>();
        return canReplaceHelper(visitedFunctions);
    }

    private boolean canReplaceHelper(HashSet<Function> visitedFunctions) {
        if (visitedFunctions.contains(this)) {
            return true;
        }
        visitedFunctions.add(this);
        // 检查参数类型是否包含指针类型
        for (FuncParam funcParam : funcParams) {
            if (funcParam.getValueType() instanceof PointerType) {
                return false;
            }
        }
        for (BasicBlock basicBlock : basicBlocks) {
            for (Instruction instruction : basicBlock.getInstructions()) {
                if (instruction instanceof Call call) {
                    // 如果是 IO 操作，返回 false
                    if (call instanceof IOInst) {
                        return false;
                    }
                    CallInst callInst = (CallInst) call;
                    Function calledFunction = callInst.getCalledFunction();
                    // 如果调用的函数不能替换，则返回 false
                    if (!calledFunction.canReplaceHelper(visitedFunctions)) {
                        return false;
                    }
                }
                // 检查操作数是否引用了非常量的全局变量
                for (Value operand : instruction.getOperands()) {
                    if (operand instanceof GlobalVar globalVar && !globalVar.isConstant()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
