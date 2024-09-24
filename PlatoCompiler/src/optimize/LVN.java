package optimize;

import middle.component.Module;
import middle.component.*;
import middle.component.instruction.*;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class LVN {
    public static HashMap<String, Instruction> map;

    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            map = new HashMap<>();
            visit(function.getBasicBlocks().get(0));
        }
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                optimizeCalc(basicBlock);
            }
        }
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                optimizeCalc(basicBlock);
            }
        }
    }

    private static void visit(BasicBlock entryBlock) {
        optimizeCalc(entryBlock);
        HashSet<Instruction> set = new HashSet<>();
        Iterator<Instruction> iterator = entryBlock.getInstructions().iterator();
        while (iterator.hasNext()) {
            Instruction instruction = iterator.next();
            String instHash = getHash(instruction);
            if (instHash == null) {
                continue;
            }
            if (map.containsKey(instHash)) {
                instruction.replaceByNewValue(map.get(instHash));
                iterator.remove();
            } else {
                map.put(instHash, instruction);
                set.add(instruction);
            }
        }
        for (BasicBlock block : entryBlock.getImmediateDominatedBlocks()) {
            visit(block);
        }
        for (Instruction instruction : set) {
            map.remove(getHash(instruction));
        }
    }

    private static void optimizeCalc(BasicBlock block) {
        Iterator<Instruction> iterator = block.getInstructions().iterator();
        while (iterator.hasNext()) {
            Instruction instruction = iterator.next();
            if (instruction instanceof LoadInst loadInst) {
                if (loadInst.getPointer() instanceof GlobalVar globalVar
                        && globalVar.isConstant()) {
                    InitialValue initialValue = globalVar.getInitialValue();
                    Value value = new ConstInt(initialValue.getValueType(),
                            initialValue.getElements().get(0));
                    loadInst.replaceByNewValue(value);
                    iterator.remove();
                } else if (loadInst.getPointer() instanceof GepInst gepInst
                        && gepInst.getPointer() instanceof GlobalVar globalVar
                        && globalVar.isConstant()
                        && gepInst.getIndex() instanceof ConstInt constInt) {
                    // 原则上index是不会有越界访问的
                    // const数组定义后内容不会发生改变，所以可以进行访存优化
                    // 与之对应的gep指令在删除死代码中可以去掉
                    int index = constInt.getIntValue();
                    InitialValue initialValue = globalVar.getInitialValue();
                    int valueInt;
                    if (index < initialValue.getElements().size()) {
                        valueInt = initialValue.getElements().get(index);
                    } else {
                        valueInt = 0;
                    }
                    ValueType valueType = ((ArrayType)initialValue.getValueType())
                            .getElementType();
                    Value value = new ConstInt(valueType, valueInt);
                    loadInst.replaceByNewValue(value);
                    iterator.remove();
                }
            }
            if (!(instruction instanceof BinaryInst binaryInst
                    && binaryInst.getValueType().equals(IntegerType.i32))) {
                continue;
            }
            Value operand1 = binaryInst.getOperand1();
            Value operand2 = binaryInst.getOperand2();
            OperatorType op = binaryInst.getOpType();
            int cnt = 0;
            if (operand1 instanceof ConstInt) {
                cnt++;
            }
            if (operand2 instanceof ConstInt) {
                cnt++;
            }
            if (cnt == 2) {
                Value ans = calcTwoConst(operand1, operand2, op);
                instruction.replaceByNewValue(ans);
                iterator.remove();
            } else if (cnt == 1) {
                Value ans = calcOneConst(operand1, operand2, op);
                if (ans != null) {
                    instruction.replaceByNewValue(ans);
                    iterator.remove();
                }
            } else {
                Value ans = calcDefault(instruction);
                if (ans != null) {
                    instruction.replaceByNewValue(ans);
                    iterator.remove();
                }
            }
        }
    }

    private static Value calcTwoConst(Value value1, Value value2, OperatorType op) {
        int lValue = ((ConstInt) value1).getIntValue();
        int rValue = ((ConstInt) value2).getIntValue();
        int ans = switch (op) {
            case ADD -> lValue + rValue;
            case SUB -> lValue - rValue;
            case MUL -> lValue * rValue;
            case SDIV -> lValue / rValue;
            case SREM -> lValue % rValue;
            default -> throw new RuntimeException("Unknown operator " + op);
        };
        return new ConstInt(IntegerType.i32, ans);
    }

    private static Value calcOneConst(Value value1, Value value2, OperatorType op) {
        switch (op) {
            case ADD -> {
                if (isZero(value1)) {
                    return value2;
                }
                if (isZero(value2)) {
                    return value1;
                }
            }
            case SUB -> {
                if (isZero(value2)) {
                    return value1;
                }
            }
            case MUL -> {
                if (isZero(value1) || isZero(value2)) {
                    return new ConstInt(IntegerType.i32, 0);
                }
                if (isOne(value1)) {
                    return value2;
                }
                if (isOne(value2)) {
                    return value1;
                }
            }
            case SDIV -> {
                if (isZero(value1)) {
                    return new ConstInt(IntegerType.i32, 0);
                }
                if (isOne(value2)) {
                    return value1;
                }
            }
            case SREM -> {
                if (isZero(value1) || isOne(value2)
                        || (value2 instanceof ConstInt constInt
                        && constInt.getIntValue() == -1)) {
                    return new ConstInt(IntegerType.i32, 0);
                }
            }
        }
        return null;
    }

    private static Value calcDefault(Instruction instruction) {
        BinaryInst binaryInst = (BinaryInst) instruction;
        Value value1 = binaryInst.getOperand1();
        Value value2 = binaryInst.getOperand2();
        OperatorType op = binaryInst.getOpType();
        switch (op) {
            case ADD -> {
                // (a - b) + b
                if (value1 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.SUB
                            && sameValue(inst.getOperand2(), value2)) {
                        return inst.getOperand1();
                    }
                }
                // b + (a - b)
                if (value2 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.SUB
                            && sameValue(inst.getOperand2(), value1)) {
                        return inst.getOperand1();
                    }
                }
                // (a + b) + (a - b)
                if (value1 instanceof BinaryInst inst1
                        && value2 instanceof BinaryInst inst2) {
                    if ((inst1.getOpType() == OperatorType.ADD
                            && inst2.getOpType() == OperatorType.SUB)
                            || (inst1.getOpType() == OperatorType.SUB
                            && inst2.getOpType() == OperatorType.ADD)) {
                        if (sameValue(inst1.getOperand2(), inst2.getOperand2())) {
                            instruction.deleteUse();
                            instruction.addOperand(inst1.getOperand1());
                            instruction.addOperand(inst2.getOperand1());
                        }
                    }
                }
            }
            case SUB -> {
                // a - a
                if (sameValue(value1, value2)) {
                    return new ConstInt(IntegerType.i32, 0);
                }
                // (a + b) - b
                // (a + b) - a
                if (value1 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.ADD) {
                        if (sameValue(inst.getOperand2(), value2)) {
                            return inst.getOperand1();
                        }
                        if (sameValue(inst.getOperand1(), value2)) {
                            return inst.getOperand2();
                        }
                    }
                }
                // a - (a - b)
                if (value2 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.SUB) {
                        if (sameValue(value1, inst.getOperand1())) {
                            return inst.getOperand2();
                        }
                    }
                }
            }
            case SDIV -> {
                if (sameValue(value1, value2)) {
                    return new ConstInt(IntegerType.i32, 1);
                }
            }
            case SREM -> {
                if (sameValue(value1, value2)) {
                    return new ConstInt(IntegerType.i32, 0);
                }
            }
        }
        return null;
    }

    private static String getHash(Instruction instruction) {
        if (instruction instanceof BinaryInst binaryInst) {
            String lName = binaryInst.getOperand1().getName();
            String rName = binaryInst.getOperand2().getName();
            String op = binaryInst.getOpType().toString();
            if (binaryInst.getOpType() == OperatorType.ADD
                    || binaryInst.getOpType() == OperatorType.MUL) {
                if (lName.compareTo(rName) > 0) {
                    return rName + " " + op + " " + lName;
                } else {
                    return lName + " " + op + " " + rName;
                }
            }
        } else if (instruction instanceof Call call) {
            if (call.getCalledFunction().isBuiltIn()
                    || call.getCalledFunction().hasSideEffect()) {
                return null;
            } else {
                return call.getCallee();
            }
        } else if (instruction instanceof GepInst gepInst) {
            return gepInst.getPointer().getName() + " "
                    + gepInst.getIndex().getName();
        }
        return null;
    }

    private static boolean isZero(Value value) {
        return (value instanceof ConstInt constInt && constInt.getIntValue() == 0);
    }

    private static boolean isOne(Value value) {
        return (value instanceof ConstInt constInt && constInt.getIntValue() == 1);
    }

    private static boolean sameValue(Value a, Value b) {
        if (a instanceof ConstInt) {
            return (b instanceof ConstInt)
                    && ((ConstInt) a).getIntValue() == ((ConstInt) b).getIntValue();
        }
        return a == b;
    }
}
