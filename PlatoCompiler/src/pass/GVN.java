package pass;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.*;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GVN {
    private static HashMap<String, Value> gvnMap = new HashMap<>();
    private static BasicBlock curBlock;
    private static HashSet<BasicBlock> deletableBlock;

    public static void run(Module module) {
        for (int i = 0; i < 10; i++) {
            optimize(module);
            SurplusBlock.build(module);
        }
    }

    private static void optimize(Module module) {
        for (Function func : module.getFunctions()) {
            gvnMap = new HashMap<>();
            visitBlock(func.getEntryBlock());
        }
        for (Function func : module.getFunctions()) {
            deletableBlock = new HashSet<>();
            for (BasicBlock block : func.getBasicBlocks()) {
                curBlock = block;
                ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof BinaryInst binaryInst
                            && binaryInst.getOpType() == OperatorType.SREM) {
                        srem2div(binaryInst);
                    }
                }
                instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instr : instructions) {
                    if (instr instanceof BinaryInst binaryInst
                            && !binaryInst.getValueType().equals(IntegerType.i1)) {
                        calcOptimize(binaryInst);
                    }
                    if (instr instanceof BinaryInst binaryInst
                            && binaryInst.getValueType().equals(IntegerType.i1)) {
                        icmpOptimize(binaryInst);
                    }
                    if (instr instanceof BrInst brInst && brInst.isConditional()) {
                        brOptimize(brInst);
                    }
                }
            }
            for (BasicBlock block : deletableBlock) {
                block.setDeleted(true);
                for (Instruction instr : block.getInstructions()) {
                    instr.deleteUse();
                }
            }
            func.getBasicBlocks().removeIf(block -> deletableBlock.contains(block));
        }
    }

    private static void visitBlock(BasicBlock block) {
        ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
        HashSet<String> inserted = new HashSet<>();
        for (Instruction instr : instructions) {
            String gvnHash = getHash(instr);
            if (gvnHash == null) {
                continue;
            }
            if (gvnMap.containsKey(gvnHash)) {
                instr.replaceByNewValue(gvnMap.get(gvnHash));
                block.getInstructions().remove(instr);
                instr.deleteUse();
            } else {
                gvnMap.put(gvnHash, instr);
                inserted.add(gvnHash);
            }
        }
        for (BasicBlock block1 : block.getImmediateDominateBlocks()) {
            visitBlock(block1);
        }
        for (String gvnHash : inserted) {
            gvnMap.remove(gvnHash);
        }
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
        } else if (instruction instanceof CallInst callInst) {
            return callInst.getCallee();
        } else if (instruction instanceof GepInst gepInst) {
            return gepInst.getPointer().getName() + " "
                    + gepInst.getIndex().getName();
        }
        return null;
    }

    private static void icmpOptimize(BinaryInst binaryInst) {
        Value value1 = binaryInst.getOperand1();
        Value value2 = binaryInst.getOperand2();
        if (value1 instanceof ConstInt constInt1 && value2 instanceof ConstInt constInt2) {
            OperatorType op = binaryInst.getOpType();
            int cons1 = constInt1.getIntValue();
            int cons2 = constInt2.getIntValue();
            int value = switch (op) {
                case ICMP_EQ -> cons1 == cons2 ? 1 : 0;
                case ICMP_NE -> cons1 != cons2 ? 1 : 0;
                case ICMP_SGE -> cons1 >= cons2 ? 1 : 0;
                case ICMP_SGT -> cons1 > cons2 ? 1 : 0;
                case ICMP_SLE -> cons1 <= cons2 ? 1 : 0;
                case ICMP_SLT -> cons1 < cons2 ? 1 : 0;
                default -> throw new IllegalStateException("Unexpected value: " + op);
            };
            ConstInt constInt = new ConstInt(IntegerType.i1, value);
            curBlock.getInstructions().remove(binaryInst);
            binaryInst.replaceByNewValue(constInt);
            binaryInst.deleteUse();
        }

    }

    private static void brOptimize(BrInst brInst) {
        Value value = brInst.getCondition();
        if (value instanceof ConstInt constInt) {
            BrInst noCondBr;
            if (constInt.getIntValue() == 0) {
                noCondBr = new BrInst(brInst.getFalseBlock());
                curBlock.deleteForPhi(brInst.getTrueBlock());
                curBlock.getNextBlocks().remove(brInst.getTrueBlock());
                brInst.getTrueBlock().getPrevBlocks().remove(curBlock);
            } else {
                noCondBr = new BrInst(brInst.getTrueBlock());
                curBlock.deleteForPhi(brInst.getTrueBlock());
                curBlock.getNextBlocks().remove(brInst.getFalseBlock());
                brInst.getFalseBlock().getPrevBlocks().remove(curBlock);
            }
            if (brInst.getTrueBlock().getPrevBlocks().isEmpty()) {
                deletableBlock.add(brInst.getTrueBlock());
            }
            if (brInst.getFalseBlock().getPrevBlocks().isEmpty()) {
                deletableBlock.add(brInst.getFalseBlock());
            }
            curBlock.getInstructions().set(curBlock.getInstructions().indexOf(brInst), noCondBr);
            brInst.deleteUse();
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
            inst.deleteUse();
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
        int ans = switch (op) {
            case ADD -> lValue + rValue;
            case SUB -> lValue - rValue;
            case MUL -> lValue * rValue;
            case SDIV -> lValue / rValue;
            case SREM -> lValue % rValue;
            default -> throw new RuntimeException("Unknown operator " + op);
        };
        ConstInt constInt = new ConstInt(IntegerType.i32, ans);
        binaryInst.replaceByNewValue(constInt);
        curBlock.getInstructions().remove(binaryInst);
        binaryInst.deleteUse();
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
            binaryInst.deleteUse();
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
                            instruction.deleteUse();
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
            binaryInst.deleteUse();
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
