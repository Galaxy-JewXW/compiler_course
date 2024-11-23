package optimize;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Module;
import middle.component.instruction.BinaryInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.OperatorType;
import middle.component.instruction.TruncInst;
import middle.component.instruction.ZextInst;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GVN {
    private static HashMap<String, Value> gvnMap = new HashMap<>();
    private static BasicBlock curBlock;
    private static HashSet<BasicBlock> deletableBlock;

    public static void run(Module module) {
        Mem2Reg.run(module, false);
        FixMD.run(module);
        optimize(module);
        SurplusBlock.build(module);
        CodeRemoval.run(module);
        // PickGep.run(module);
        PhiOptimize.run(module);
    }

    private static void optimize(Module module) {
        module.getFunctions().forEach(func -> {
            gvnMap = new HashMap<>();
            visitBlock(func.getEntryBlock());
        });

        module.getFunctions().forEach(func -> {
            deletableBlock = new HashSet<>();
            func.getBasicBlocks().forEach(block -> {
                curBlock = block;
                optimizeBlock(block);
            });

            func.getBasicBlocks().removeIf(block -> {
                if (deletableBlock.contains(block)) {
                    block.setDeleted(true);
                    block.getInstructions().forEach(Instruction::removeOperands);
                    return true;
                }
                return false;
            });
        });
    }

    private static void optimizeBlock(BasicBlock block) {
        ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
        instructions.stream()
                .filter(instruction -> instruction instanceof BinaryInst)
                .map(instruction -> (BinaryInst) instruction)
                .filter(binaryInst -> binaryInst.getOpType() == OperatorType.SREM)
                .forEach(GVN::srem2div);

        instructions = new ArrayList<>(block.getInstructions());
        instructions.forEach(instr -> {
            if (instr instanceof BinaryInst binaryInst) {
                if (!OperatorType.isLogicalOperator(binaryInst.getOpType())) {
                    calcOptimize(binaryInst);
                }
            } else if (instr instanceof ZextInst zextInst) {
                zextOptimize(zextInst);
            } else if (instr instanceof TruncInst truncInst) {
                truncOptimize(truncInst);
            }
        });
    }

    private static void visitBlock(BasicBlock block) {
        ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
        HashSet<String> inserted = new HashSet<>();

        instructions.forEach(instr -> {
            String gvnHash = getHash(instr);
            if (gvnHash != null) {
                if (gvnMap.containsKey(gvnHash)) {
                    instr.replaceByNewValue(gvnMap.get(gvnHash));
                    block.getInstructions().remove(instr);
                    instr.removeOperands();
                } else {
                    gvnMap.put(gvnHash, instr);
                    inserted.add(gvnHash);
                }
            }
        });

        block.getImmediateDominateBlocks().forEach(GVN::visitBlock);
        inserted.forEach(gvnMap::remove);
    }

    private static String getHash(Instruction instruction) {
        if (instruction instanceof BinaryInst binaryInst) {
            String lName = binaryInst.getOperand1().getName();
            String rName = binaryInst.getOperand2().getName();
            String op = binaryInst.getOpType().toString();
            if (!OperatorType.isLogicalOperator(binaryInst.getOpType())) {
                if (binaryInst.getOpType() == OperatorType.ADD
                        || binaryInst.getOpType() == OperatorType.MUL) {
                    if (lName.compareTo(rName) > 0) {
                        return rName + " " + op + " " + lName;
                    } else {
                        return lName + " " + op + " " + rName;
                    }
                }
            } else {
                OperatorType temp = binaryInst.getOpType();
                if (lName.compareTo(rName) < 0) {
                    lName = binaryInst.getOperand2().getName();
                    rName = binaryInst.getOperand1().getName();
                    temp = switch (temp) {
                        case ICMP_SGE -> OperatorType.ICMP_SLE;
                        case ICMP_SGT -> OperatorType.ICMP_SLT;
                        case ICMP_SLT -> OperatorType.ICMP_SGT;
                        case ICMP_SLE -> OperatorType.ICMP_SGE;
                        default -> temp;
                    };
                }
                return lName + " " + temp.toString() + " " + rName;
            }
        } else if (instruction instanceof GepInst gepInst) {
            return gepInst.getPointer().getName() + " "
                    + gepInst.getIndex().getName();
        } else if (instruction instanceof CallInst callInst && callCanReplaced(callInst)) {
            return callInst.getCallee();
        }
        return null;
    }

    private static boolean callCanReplaced(CallInst callInst) {
        return callInst.getCalledFunction().canReplace();
    }

    private static void zextOptimize(ZextInst zextInst) {
        Value origin = zextInst.getOriginValue();
        if (origin instanceof ConstInt constInt) {
            int value = constInt.getIntValue();
            ValueType targetType = zextInst.getValueType();
            if (targetType.equals(IntegerType.i8) || targetType.equals(IntegerType.i32)) {
                ConstInt constInt1 = new ConstInt(targetType, value);
                zextInst.replaceByNewValue(constInt1);
                zextInst.removeOperands();
                curBlock.getInstructions().remove(zextInst);
            }
        }
    }

    private static void truncOptimize(TruncInst truncInst) {
        Value origin = truncInst.getOriginValue();
        if (origin instanceof ConstInt constInt) {
            int value = constInt.getIntValue();
            ValueType targetType = truncInst.getValueType();
            if (targetType.equals(IntegerType.i8) && constInt.getValueType().equals(IntegerType.i32)) {
                ConstInt constInt1 = new ConstInt(targetType, value);
                truncInst.replaceByNewValue(constInt1);
                truncInst.removeOperands();
                curBlock.getInstructions().remove(truncInst);
            }
        }
    }

    // a % b = a - a / b * b;
    // 这里要求b是一个ConstInt，后端翻译除法指令的时候可以继续优化
    private static void srem2div(BinaryInst inst) {
        Value a = inst.getOperand1();
        Value b = inst.getOperand2();
        BasicBlock curBlock = inst.getBasicBlock();
        if (!(a instanceof ConstInt) && (b instanceof ConstInt)) {
            BinaryInst div = new BinaryInst(OperatorType.SDIV, a, b);
            BinaryInst mul = new BinaryInst(OperatorType.MUL, div, b);
            BinaryInst sub = new BinaryInst(OperatorType.SUB, a, mul);
            div.setBasicBlock(curBlock);
            mul.setBasicBlock(curBlock);
            sub.setBasicBlock(curBlock);
            curBlock.getInstructions().set(curBlock.getInstructions().indexOf(inst), div);
            curBlock.getInstructions().add(curBlock.getInstructions().indexOf(div) + 1, mul);
            curBlock.getInstructions().add(curBlock.getInstructions().indexOf(mul) + 1, sub);
            inst.replaceByNewValue(sub);
            curBlock.getInstructions().remove(inst);
            inst.removeOperands();
        }
    }

    private static void calcOptimize(BinaryInst binaryInst) {
        int constCnt = 0;
        for (Value value : binaryInst.getOperands()) {
            if (value instanceof ConstInt) {
                constCnt++;
            }
        }
        if (constCnt == 2) {
            calcTwoConst(binaryInst);
        } else if (constCnt == 1) {
            calcOneConst(binaryInst);
        } else {
            calcDefault(binaryInst);
        }
    }

    private static void calcTwoConst(BinaryInst binaryInst) {
        int lValue = ((ConstInt) binaryInst.getOperand1()).getIntValue();
        int rValue = ((ConstInt) binaryInst.getOperand2()).getIntValue();
        OperatorType op = binaryInst.getOpType();
        int ans;
        try {
            ans = switch (op) {
                case ADD -> lValue + rValue;
                case SUB -> lValue - rValue;
                case MUL -> lValue * rValue;
                case SDIV -> lValue / rValue;
                case SREM -> lValue % rValue;
                default -> throw new RuntimeException("Unknown operator " + op);
            };
        } catch (ArithmeticException e) {
            ans = 0;
        }
        ConstInt constInt = new ConstInt(IntegerType.i32, ans);
        binaryInst.replaceByNewValue(constInt);
        curBlock.getInstructions().remove(binaryInst);
        binaryInst.removeOperands();
    }

    private static void calcOneConst(BinaryInst binaryInst) {
        Value value1 = binaryInst.getOperand1();
        Value value2 = binaryInst.getOperand2();
        OperatorType op = binaryInst.getOpType();
        Value value = switch (op) {
            case ADD -> {
                if (isZero(value1)) {
                    yield value2;
                }
                if (isZero(value2)) {
                    yield value1;
                }
                yield null; // 如果两个值都不为零，则不进行优化
            }
            case SUB -> {
                if (isZero(value2)) {
                    yield value1;
                }
                yield null; // 如果减数不为零，则不进行优化
            }
            case MUL -> {
                if (isZero(value1) || isZero(value2)) {
                    yield new ConstInt(IntegerType.i32, 0);
                }
                if (isOne(value1)) {
                    yield value2;
                }
                if (isOne(value2)) {
                    yield value1;
                }
                yield null; // 如果两个值都不为零或一，则不进行优化
            }
            case SDIV -> {
                if (isZero(value1)) {
                    yield new ConstInt(IntegerType.i32, 0);
                }
                if (isOne(value2)) {
                    yield value1;
                }
                yield null; // 如果被除数不为零且除数不为一，则不进行优化
            }
            case SREM -> {
                if (isZero(value1) || isOne(value2)
                        || (value2 instanceof ConstInt constInt
                        && constInt.getIntValue() == -1)) {
                    yield new ConstInt(IntegerType.i32, 0);
                }
                yield null; // 如果不满足上述条件，则不进行优化
            }
            default -> null; // 对于其他操作，不进行优化
        };
        if (value != null) {
            binaryInst.replaceByNewValue(value);
            curBlock.getInstructions().remove(binaryInst);
            binaryInst.removeOperands();
        }
    }

    private static void calcDefault(Instruction instruction) {
        BinaryInst binaryInst = (BinaryInst) instruction;
        Value value1 = binaryInst.getOperand1();
        Value value2 = binaryInst.getOperand2();
        OperatorType op = binaryInst.getOpType();
        Value value = switch (op) {
            case ADD -> {
                // (a - b) + b
                if (value1 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.SUB
                            && sameValue(inst.getOperand2(), value2)) {
                        yield inst.getOperand1();
                    }
                }
                // b + (a - b)
                if (value2 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.SUB
                            && sameValue(inst.getOperand2(), value1)) {
                        yield inst.getOperand1();
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
                            instruction.removeOperands();
                            instruction.addOperand(inst1.getOperand1());
                            instruction.addOperand(inst2.getOperand1());
                            yield instruction;
                        }
                    }
                }
                yield null;
            }
            case SUB -> {
                // a - a
                if (sameValue(value1, value2)) {
                    yield new ConstInt(IntegerType.i32, 0);
                }
                // (a + b) - b
                // (a + b) - a
                if (value1 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.ADD) {
                        if (sameValue(inst.getOperand2(), value2)) {
                            yield inst.getOperand1();
                        }
                        if (sameValue(inst.getOperand1(), value2)) {
                            yield inst.getOperand2();
                        }
                    }
                }
                // a - (a - b)
                if (value2 instanceof BinaryInst inst) {
                    if (inst.getOpType() == OperatorType.SUB) {
                        if (sameValue(value1, inst.getOperand1())) {
                            yield inst.getOperand2();
                        }
                    }
                }
                yield null;
            }
            case SDIV -> {
                if (sameValue(value1, value2)) {
                    yield new ConstInt(IntegerType.i32, 1);
                }
                yield null;
            }
            case SREM -> {
                if (sameValue(value1, value2)) {
                    yield new ConstInt(IntegerType.i32, 0);
                }
                yield null;
            }
            default -> null;
        };
        if (value != null) {
            binaryInst.replaceByNewValue(value);
            curBlock.getInstructions().remove(binaryInst);
            binaryInst.removeOperands();
        }
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
