package optimize;

import middle.component.Module;
import middle.component.*;
import middle.component.instruction.*;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class GVN {
    private static HashMap<String, Instruction> map;
    private static HashSet<BasicBlock> deletingBlock;

    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            map = new HashMap<>();
            visit(function.getBasicBlocks().get(0));
        }
        for (int i = 0; i < 5; i++) {
            for (Function function : module.getFunctions()) {
                deletingBlock = new HashSet<>();
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    ArrayList<Instruction> list =
                            new ArrayList<>(basicBlock.getInstructions());
                    for (Instruction instruction : list) {
                        if (instruction instanceof BinaryInst binaryInst
                                && binaryInst.getOpType() == OperatorType.SREM) {
                            srem2div(binaryInst);
                        }
                    }
                }
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    optimizeCalc(basicBlock);
                }
                for (BasicBlock basicBlock : deletingBlock) {
                    for (Instruction instruction : basicBlock.getInstructions()) {
                        instruction.deleteUse();
                    }
                }
                function.getBasicBlocks().removeIf(basicBlock
                        -> deletingBlock.contains(basicBlock));
            }
        }
    }

    // a % b = a - a / b * b;
    // 这里要求b是一个ConstInt，后端翻译除法指令的时候可以继续优化
    private static void srem2div(BinaryInst inst) {
        Value a = inst.getOperand1();
        Value b = inst.getOperand2();
        BasicBlock curBlock = inst.getBasicBlock();
        if (!(a instanceof ConstInt) && (b instanceof ConstInt constInt)) {
            BinaryInst div = new BinaryInst(OperatorType.SDIV, a, b, curBlock);
            BinaryInst mul = new BinaryInst(OperatorType.MUL, div, b, curBlock);
            BinaryInst sub = new BinaryInst(OperatorType.SUB, a, mul, curBlock);
            curBlock.getInstructions().set(curBlock.getInstructions().indexOf(inst), div);
            curBlock.getInstructions().add(curBlock.getInstructions().indexOf(div) + 1, mul);
            curBlock.getInstructions().add(curBlock.getInstructions().indexOf(mul) + 1, sub);
            inst.replaceByNewValue(sub);
            curBlock.getInstructions().remove(inst);
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
                    ValueType valueType = ((ArrayType) initialValue.getValueType())
                            .getElementType();
                    Value value = new ConstInt(valueType, valueInt);
                    loadInst.replaceByNewValue(value);
                    iterator.remove();
                }
            }
            if (instruction instanceof BinaryInst binaryInst
                    && binaryInst.getValueType().equals(IntegerType.i32)) {
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
            } else if (instruction instanceof BinaryInst binaryInst
                    && binaryInst.getValueType().equals(IntegerType.i1)) {
                OperatorType op = binaryInst.getOpType();
                if (op == OperatorType.ICMP_SLE || op == OperatorType.ICMP_SLT
                        || op == OperatorType.ICMP_SGE || op == OperatorType.ICMP_SGT) {
                    Value operand1 = binaryInst.getOperand1();
                    Value operand2 = binaryInst.getOperand2();
                    if (operand1 instanceof ConstInt constInt1
                            && operand2 instanceof ConstInt constInt2) {
                        ConstInt newInt = getRelInt(constInt1, constInt2, op);
                        instruction.replaceByNewValue(newInt);
                        iterator.remove();
                    }
                } else if (op == OperatorType.ICMP_EQ || op == OperatorType.ICMP_NE) {
                    Value operand1 = binaryInst.getOperand1();
                    Value operand2 = binaryInst.getOperand2();
                    if (operand1 instanceof ConstInt constInt1
                            && operand2 instanceof ConstInt constInt2) {
                        ConstInt newInt = getEqInt(constInt1, constInt2, op);
                        instruction.replaceByNewValue(newInt);
                        iterator.remove();
                    } else if (operand1 instanceof ConstInt constInt1
                            && operand2 instanceof ZextInst zextInst) {
                        if (zextInst.getOriginValue() instanceof ConstInt constInt2) {
                            ConstInt newInt = getEqInt(constInt1, constInt2, op);
                            instruction.replaceByNewValue(newInt);
                            iterator.remove();
                        }
                    } else if (operand1 instanceof ZextInst zextInst
                            && operand2 instanceof ConstInt constInt2) {
                        if (zextInst.getOriginValue() instanceof ConstInt constInt1) {
                            ConstInt newInt = getEqInt(constInt1, constInt2, op);
                            instruction.replaceByNewValue(newInt);
                            iterator.remove();
                        }
                    } else if (operand1 instanceof ZextInst zextInst1
                            && operand2 instanceof ZextInst zextInst2) {
                        if (zextInst1.getOriginValue() instanceof ConstInt constInt1
                                && zextInst2.getOriginValue() instanceof ConstInt constInt2) {
                            ConstInt newInt = getEqInt(constInt1, constInt2, op);
                            instruction.replaceByNewValue(newInt);
                            iterator.remove();
                        }
                    }
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

    private static ConstInt getRelInt(ConstInt constInt1, ConstInt constInt2, OperatorType op) {
        int left = constInt1.getIntValue();
        int right = constInt2.getIntValue();
        int ans = switch (op) {
            case ICMP_SLT -> left < right ? 1 : 0;
            case ICMP_SGT -> left > right ? 1 : 0;
            case ICMP_SGE -> left <= right ? 1 : 0;
            case ICMP_SLE -> left >= right ? 1 : 0;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + op);
        };
        return new ConstInt(IntegerType.i1, ans);
    }

    private static ConstInt getEqInt(ConstInt constInt1, ConstInt constInt2,
                                     OperatorType op) {
        int left = constInt1.getIntValue();
        int right = constInt2.getIntValue();
        int ans = switch (op) {
            case ICMP_EQ -> left == right ? 1 : 0;
            case ICMP_NE -> left != right ? 1 : 0;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + op);
        };
        return new ConstInt(IntegerType.i1, ans);
    }
}
