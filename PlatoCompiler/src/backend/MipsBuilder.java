package backend;

import backend.enums.AsmOp;
import backend.enums.Register;
import backend.global.Asciiz;
import backend.global.Word;
import backend.text.BrAsm;
import backend.text.CalcAsm;
import backend.text.CmpAsm;
import backend.text.Comment;
import backend.text.JumpAsm;
import backend.text.LaAsm;
import backend.text.Label;
import backend.text.LiAsm;
import backend.text.MDRegAsm;
import backend.text.MemAsm;
import backend.text.MoveAsm;
import backend.text.MulDivAsm;
import backend.text.NegAsm;
import backend.text.SyscallAsm;
import backend.utils.OptimizedDivision;
import backend.utils.PeepHole;
import backend.utils.RegAlloc;
import backend.utils.RemovePhi;
import backend.utils.ZextRemoval;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.ConstString;
import middle.component.FuncParam;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.instruction.AllocInst;
import middle.component.instruction.BinaryInst;
import middle.component.instruction.BrInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.LoadInst;
import middle.component.instruction.MoveInst;
import middle.component.instruction.OperatorType;
import middle.component.instruction.RetInst;
import middle.component.instruction.StoreInst;
import middle.component.instruction.TruncInst;
import middle.component.instruction.ZextInst;
import middle.component.instruction.io.GetcharInst;
import middle.component.instruction.io.GetintInst;
import middle.component.instruction.io.PutchInst;
import middle.component.instruction.io.PutintInst;
import middle.component.instruction.io.PutstrInst;
import middle.component.model.User;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

public class MipsBuilder {
    private final Module module;
    private final Map<Class<? extends Instruction>,
            Consumer<Instruction>> instructionHandlers = new HashMap<>();
    private final boolean optimizeOn;
    private int curStackOffset;
    private HashMap<Value, Register> var2reg;
    private boolean isInMain = false;
    private Function currentFunction;
    private HashMap<Value, Integer> var2Offset;

    public MipsBuilder(Module module, boolean optimizeOn) {
        this.module = module;
        this.optimizeOn = optimizeOn;
        if (optimizeOn) {
            ZextRemoval.run(module);
            new RegAlloc().run(module);
            RemovePhi.run(module);
            module.updateId();
        }
        initInstructionHandlers();
    }

    private void initInstructionHandlers() {
        instructionHandlers.put(AllocInst.class, inst -> buildAllocInst((AllocInst) inst));
        instructionHandlers.put(BinaryInst.class, inst -> {
            BinaryInst binaryInst = (BinaryInst) inst;
            if (OperatorType.isLogicalOperator(binaryInst.getOpType())) {
                buildIcmp(binaryInst);
            } else {
                buildBinaryInst(binaryInst);
            }
        });
        instructionHandlers.put(BrInst.class, inst -> {
            BrInst brInst = (BrInst) inst;
            if (brInst.isConditional()) {
                buildCondBrInst(brInst);
            } else {
                buildNoCondBrInst(brInst);
            }
        });
        instructionHandlers.put(CallInst.class, inst -> buildCallInst((CallInst) inst));
        instructionHandlers.put(GepInst.class, inst -> buildGepInst((GepInst) inst));
        instructionHandlers.put(LoadInst.class, inst -> buildLoadInst((LoadInst) inst));
        instructionHandlers.put(MoveInst.class, inst -> buildMoveInst((MoveInst) inst));
        instructionHandlers.put(GetintInst.class, inst -> buildGetintInst((GetintInst) inst));
        instructionHandlers.put(GetcharInst.class, inst -> buildGetcharInst((GetcharInst) inst));
        instructionHandlers.put(PutintInst.class, inst -> buildPutintInst((PutintInst) inst));
        instructionHandlers.put(PutchInst.class, inst -> buildPutchInst((PutchInst) inst));
        instructionHandlers.put(PutstrInst.class, inst -> buildPutstrInst((PutstrInst) inst));
        instructionHandlers.put(RetInst.class, inst -> buildRetInst((RetInst) inst));
        instructionHandlers.put(StoreInst.class, inst -> buildStoreInst((StoreInst) inst));
        instructionHandlers.put(TruncInst.class, inst -> buildTruncInst((TruncInst) inst));
        instructionHandlers.put(ZextInst.class, inst -> buildZextInst((ZextInst) inst));
    }

    public void build(boolean optimize) {
        for (ConstString constString : module.getConstStrings()) {
            buildConstString(constString);
        }
        for (GlobalVar globalVar : module.getGlobalVars()) {
            buildGlobalVar(globalVar);
        }
        isInMain = true;
        for (Function function : module.getFunctions()) {
            if (function.getName().equals("@main")) {
                buildFunction(function);
                break;
            }
        }
        isInMain = false;
        for (Function function : module.getFunctions()) {
            if (!function.getName().equals("@main")) {
                buildFunction(function);
            }
        }
        if (optimize) {
            PeepHole.run();
        }
    }

    private void buildConstString(ConstString constString) {
        new Asciiz("s" + constString.getName().substring(4),
                constString.getContent());
    }

    private void buildGlobalVar(GlobalVar globalVar) {
        ValueType targetType = ((PointerType) globalVar.getValueType())
                .getTargetType();
        if (targetType instanceof IntegerType) {
            if (globalVar.getInitialValue().getElements() == null) {
                new Word(globalVar.getName().substring(1), 0);
            } else {
                new Word(globalVar.getName().substring(1),
                        globalVar.getInitialValue().getElements().get(0));
            }
        } else if (targetType instanceof ArrayType) {
            // 初始化全局数组
            ArrayList<Integer> list = globalVar.getInitialValue().getElements();
            int length = globalVar.getInitialValue().getLength();
            new Word(globalVar.getName().substring(1), list, length);
        } else {
            throw new RuntimeException("Unknown global variable type: " + targetType);
        }
    }

    private void buildFunction(Function function) {
        currentFunction = function;
        var2Offset = new HashMap<>();
        curStackOffset = 0;
        var2reg = optimizeOn ? new HashMap<>(function.getVar2reg()) : new HashMap<>();
        new Label("func_" + function.getName().substring(1));
        for (int i = 0; i < function.getFuncParams().size(); i++) {
            curStackOffset -= 4;
            if (i < 3) {
                // a1, a2, a3
                var2reg.put(function.getFuncParams().get(i),
                        Register.getByOffset(Register.A1, i));
            }
            var2Offset.put(function.getFuncParams().get(i), curStackOffset);
        }
        for (BasicBlock block : function.getBasicBlocks()) {
            for (Instruction instruction : block.getInstructions()) {
                if (!instruction.getName().isEmpty()
                        && !(instruction instanceof MoveInst)
                        && !var2reg.containsKey(instruction)
                        && !var2Offset.containsKey(instruction)) {
                    curStackOffset -= 4;
                    var2Offset.put(instruction, curStackOffset);
                } else if (instruction instanceof MoveInst moveInst) {
                    if (!var2reg.containsKey(moveInst.getToValue())
                            && !var2Offset.containsKey(moveInst.getToValue())) {
                        curStackOffset -= 4;
                        var2Offset.put(moveInst.getToValue(), curStackOffset);
                    }
                }
            }
        }
        for (BasicBlock block : function.getBasicBlocks()) {
            buildBasicBlock(block);
        }
    }

    private void buildBasicBlock(BasicBlock block) {
        new Label(currentFunction.getName().substring(1)
                + "_b" + block.getName());
        for (Instruction instruction : block.getInstructions()) {
            new Comment("# " + instruction.toString());
            buildInstruction(instruction);
        }
    }

    private void buildInstruction(Instruction instruction) {
        Consumer<Instruction> handler = instructionHandlers.get(instruction.getClass());
        if (handler != null) {
            handler.accept(instruction);
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported instruction: " + instruction.getClass());
        }
    }

    private void buildAllocInst(AllocInst allocInst) {
        ValueType targetType = ((PointerType) allocInst.getValueType()).getTargetType();
        if (targetType instanceof ArrayType arrayType) {
            curStackOffset -= 4 * arrayType.getElementNum();
        } else {
            curStackOffset -= 4;
        }
        if (var2reg.containsKey(allocInst)) {
            new CalcAsm(var2reg.get(allocInst), AsmOp.ADDIU, Register.SP, curStackOffset);
        } else {
            new CalcAsm(Register.K0, AsmOp.ADDIU, Register.SP, curStackOffset);
            new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(allocInst));
        }
    }

    private void buildIcmp(BinaryInst binaryInst) {
        boolean flag = true;
        for (User user : binaryInst.getUserList()) {
            if (!(user instanceof BrInst)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            return;
        }
        AsmOp asmOp = switch (binaryInst.getOpType()) {
            case ICMP_EQ -> AsmOp.SEQ;
            case ICMP_NE -> AsmOp.SNE;
            case ICMP_SLT -> AsmOp.SLT;
            case ICMP_SLE -> AsmOp.SLE;
            case ICMP_SGT -> AsmOp.SGT;
            case ICMP_SGE -> AsmOp.SGE;
            default -> throw new IllegalStateException("Unexpected value: "
                    + binaryInst.getOpType());
        };
        Value operand1 = binaryInst.getOperand1();
        Value operand2 = binaryInst.getOperand2();
        Register reg1 = Register.K0;
        Register reg2 = Register.K1;
        if (operand1 instanceof ConstInt constInt) {
            new LiAsm(reg1, constInt.getIntValue());
        } else if (var2reg.containsKey(operand1)) {
            reg1 = var2reg.get(operand1);
        } else {
            new MemAsm(AsmOp.LW, reg1, Register.SP, var2Offset.get(operand1));
        }
        if (operand2 instanceof ConstInt constInt) {
            new LiAsm(reg2, constInt.getIntValue());
        } else if (var2reg.containsKey(operand2)) {
            reg2 = var2reg.get(operand2);
        } else {
            new MemAsm(AsmOp.LW, reg2, Register.SP, var2Offset.get(operand2));
        }
        if (var2reg.containsKey(binaryInst)) {
            new CmpAsm(var2reg.get(binaryInst), asmOp, reg1, reg2);
        } else {
            new CmpAsm(Register.K0, asmOp, reg1, reg2);
            new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(binaryInst));
        }
    }

    private void buildBinaryInst(BinaryInst binaryInst) {
        Value operand1 = binaryInst.getOperand1();
        Value operand2 = binaryInst.getOperand2();
        int cnt = 0;
        if (operand1 instanceof ConstInt) {
            cnt++;
        }
        if (operand2 instanceof ConstInt) {
            cnt++;
        }
        Register targetReg = var2reg.getOrDefault(binaryInst, Register.K0);
        if (cnt == 2) {
            makeTwoConst(binaryInst, targetReg);
        } else if (cnt == 1) {
            makeOneConst(binaryInst, targetReg);
        } else {
            makeNonConst(binaryInst, targetReg);
        }
        if (targetReg.equals(Register.K0)) {
            // variable in stack
            new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(binaryInst));
        }
    }

    private void makeTwoConst(BinaryInst binaryInst, Register targetReg) {
        ConstInt constInt1 = (ConstInt) binaryInst.getOperand1();
        ConstInt constInt2 = (ConstInt) binaryInst.getOperand2();
        new LiAsm(Register.K0, constInt1.getIntValue());
        OperatorType op = binaryInst.getOpType();
        if (op == OperatorType.ADD || op == OperatorType.SUB) {
            int number = op == OperatorType.ADD
                    ? constInt2.getIntValue() : -constInt2.getIntValue();
            new CalcAsm(targetReg, AsmOp.ADDIU, Register.K0, number);
        } else {
            new LiAsm(Register.K1, constInt2.getIntValue());
            if (op == OperatorType.MUL) {
                new CalcAsm(targetReg, AsmOp.MUL, Register.K0, Register.K1);
            } else {
                new MulDivAsm(Register.K0, AsmOp.DIV, Register.K1);
                if (op == OperatorType.SDIV) {
                    new MDRegAsm(AsmOp.MFLO, targetReg);
                } else if (op == OperatorType.SREM) {
                    new MDRegAsm(AsmOp.MFHI, targetReg);
                } else {
                    throw new RuntimeException("Unknown operator " + op);
                }
            }
        }
    }

    private void makeOneConst(BinaryInst binaryInst, Register targetReg) {
        Value operand1 = binaryInst.getOperand1();
        Value operand2 = binaryInst.getOperand2();
        if (operand1 instanceof ConstInt constInt) {
            // 100 op a
            Register temp = Register.K0;
            if (var2reg.containsKey(operand2)) {
                temp = var2reg.get(operand2);
            } else {
                new MemAsm(AsmOp.LW, temp, Register.SP, var2Offset.get(operand2));
            }
            if (binaryInst.getOpType() == OperatorType.ADD) {
                new CalcAsm(targetReg, AsmOp.ADDIU, temp, constInt.getIntValue());
            } else if (binaryInst.getOpType() == OperatorType.MUL) {
                int constant = constInt.getIntValue();
                makeVarMulConst(temp, constant, targetReg);
            } else {
                if (binaryInst.getOpType() == OperatorType.SUB && constInt.getIntValue() == 0) {
                    new NegAsm(targetReg, temp);
                    return;
                }
                new LiAsm(Register.K1, constInt.getIntValue());
                if (binaryInst.getOpType() == OperatorType.SUB) {
                    new CalcAsm(targetReg, AsmOp.SUBU, Register.K1, temp);
                    return;
                }
                new MulDivAsm(Register.K1, AsmOp.DIV, temp);
                if (binaryInst.getOpType() == OperatorType.SDIV) {
                    new MDRegAsm(AsmOp.MFLO, targetReg);
                } else if (binaryInst.getOpType() == OperatorType.SREM) {
                    new MDRegAsm(AsmOp.MFHI, targetReg);
                } else {
                    throw new RuntimeException("Unknown operator "
                            + binaryInst.getOpType());
                }
            }
        } else if (operand2 instanceof ConstInt constInt) {
            // a op 100
            Register temp = Register.K0;
            if (var2reg.containsKey(operand1)) {
                temp = var2reg.get(operand1);
            } else {
                new MemAsm(AsmOp.LW, temp, Register.SP, var2Offset.get(operand1));
            }
            int constant = constInt.getIntValue();
            if (binaryInst.getOpType() == OperatorType.ADD) {
                new CalcAsm(targetReg, AsmOp.ADDIU, temp, constant);
            } else if (binaryInst.getOpType() == OperatorType.SUB) {
                new CalcAsm(targetReg, AsmOp.ADDIU, temp, -constant);
            } else if (binaryInst.getOpType() == OperatorType.SFUCK) {
                new CalcAsm(targetReg, AsmOp.ADDIU, temp, constant);
                new MoveAsm(Register.K0, targetReg);
                for (int i = 1; i < constant; i++) {
                    new CalcAsm(targetReg, AsmOp.MUL, Register.K0, targetReg);
                }
            } else if (binaryInst.getOpType() == OperatorType.MUL) {
                makeVarMulConst(temp, constant, targetReg);
            } else if (binaryInst.getOpType() == OperatorType.SREM) {
                new LiAsm(Register.K1, constInt.getIntValue());
                new MulDivAsm(temp, AsmOp.DIV, Register.K1);
                new MDRegAsm(AsmOp.MFHI, targetReg);
            } else if (binaryInst.getOpType() == OperatorType.SDIV) {
                OptimizedDivision.makeVarDivConst(temp, constant, targetReg);
            } else {
                throw new RuntimeException("Unknown operator " + binaryInst.getOpType());
            }
        }
    }

    private void makeNonConst(BinaryInst binaryInst, Register targetReg) {
        Value operand1 = binaryInst.getOperand1();
        Value operand2 = binaryInst.getOperand2();
        Register reg1 = Register.K0;
        Register reg2 = Register.K1;
        if (var2reg.containsKey(operand1)) {
            reg1 = var2reg.get(operand1);
        } else {
            new MemAsm(AsmOp.LW, reg1, Register.SP, var2Offset.get(operand1));
        }
        if (var2reg.containsKey(operand2)) {
            reg2 = var2reg.get(operand2);
        } else {
            new MemAsm(AsmOp.LW, reg2, Register.SP, var2Offset.get(operand2));
        }
        switch (binaryInst.getOpType()) {
            case ADD -> new CalcAsm(targetReg, AsmOp.ADDU, reg1, reg2);
            case SUB -> new CalcAsm(targetReg, AsmOp.SUBU, reg1, reg2);
            case MUL -> new CalcAsm(targetReg, AsmOp.MUL, reg1, reg2);
            case SDIV -> {
                new MulDivAsm(reg1, AsmOp.DIV, reg2);
                new MDRegAsm(AsmOp.MFLO, targetReg);
            }
            case SREM -> {
                new MulDivAsm(reg1, AsmOp.DIV, reg2);
                new MDRegAsm(AsmOp.MFHI, targetReg);
            }
        }
    }

    private void makeVarMulConst(Register varReg, int constInt, Register targetReg) {
        if (constInt == 0) {
            new LiAsm(targetReg, 0);
            return;
        }
        if (constInt == 1) {
            new MoveAsm(targetReg, varReg);
            return;
        }
        if (constInt == -1) {
            new NegAsm(targetReg, varReg);
            return;
        }
        boolean isNegative = constInt < 0;
        int absConstInt = Math.abs(constInt);
        switch (absConstInt) {
            case 2:
                new CalcAsm(targetReg, AsmOp.ADDU, varReg, varReg);
                break;
            case 3:
                new CalcAsm(Register.V0, AsmOp.ADDU, varReg, varReg);
                new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, varReg);
                break;
            case 4:
                new CalcAsm(targetReg, AsmOp.SLL, varReg, 2);
                break;
            case 5:
                new CalcAsm(Register.V0, AsmOp.SLL, varReg, 2);
                new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, varReg);
                break;
            case 6:
                new CalcAsm(Register.V0, AsmOp.SLL, varReg, 2);
                new CalcAsm(Register.V1, AsmOp.ADDU, varReg, varReg);
                new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, Register.V1);
                break;
            case 7:
                new CalcAsm(Register.V0, AsmOp.SLL, varReg, 3);
                new CalcAsm(targetReg, AsmOp.SUBU, Register.V0, varReg);
                break;
            case 8:
                new CalcAsm(targetReg, AsmOp.SLL, varReg, 3);
                break;
            case 9:
                new CalcAsm(Register.V0, AsmOp.SLL, varReg, 3);
                new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, varReg);
                break;
            default:
                int bitCnt = Integer.bitCount(absConstInt);
                if (bitCnt <= 2) {
                    // 使用原有的移位和加法逻辑
                    int[] shifts = new int[2];
                    int index = 0;
                    for (int i = 0; i < 32; i++) {
                        if ((absConstInt & (1 << i)) != 0) {
                            shifts[index++] = i;
                            if (index == 2) break;
                        }
                    }

                    if (bitCnt == 1) {
                        new CalcAsm(targetReg, AsmOp.SLL, varReg, shifts[0]);
                    } else {
                        new CalcAsm(Register.V0, AsmOp.SLL, varReg, shifts[0]);
                        new CalcAsm(Register.V1, AsmOp.SLL, varReg, shifts[1]);
                        new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, Register.V1);
                    }
                } else {
                    // 对于其他情况，使用乘法
                    new LiAsm(Register.V0, absConstInt);
                    new CalcAsm(targetReg, AsmOp.MUL, varReg, Register.V0);
                }
        }
        if (isNegative) {
            new NegAsm(targetReg, targetReg);
        }
    }

    private void buildCondBrInst(BrInst brInst) {
        BinaryInst condition = (BinaryInst) brInst.getCondition();
        boolean flag = true;
        for (User user : condition.getUserList()) {
            if (!(user instanceof BrInst)) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            if (var2reg.containsKey(condition)) {
                new BrAsm(currentFunction.getName().substring(1)
                        + "_b" + brInst.getTrueBlock().getName(),
                        var2reg.get(condition), AsmOp.BEQ, 1);
            } else {
                new MemAsm(AsmOp.LW, Register.K0, Register.SP, var2Offset.get(condition));
                new BrAsm(currentFunction.getName().substring(1)
                        + "_b" + brInst.getTrueBlock().getName(),
                        Register.K0, AsmOp.BEQ, 1);
            }
        } else {
            AsmOp asmOp = switch (condition.getOpType()) {
                case ICMP_EQ -> AsmOp.BEQ;
                case ICMP_NE -> AsmOp.BNE;
                case ICMP_SLT -> AsmOp.BLT;
                case ICMP_SLE -> AsmOp.BLE;
                case ICMP_SGT -> AsmOp.BGT;
                case ICMP_SGE -> AsmOp.BGE;
                default -> throw new IllegalStateException("Unexpected value: "
                        + condition.getOpType());
            };
            Value operand1 = condition.getOperand1();
            Value operand2 = condition.getOperand2();
            Register reg1 = Register.K0;
            Register reg2 = Register.K1;
            ConstInt constInt1 = null;
            ConstInt constInt2 = null;
            if (operand1 instanceof ConstInt constInt) {
                constInt1 = constInt;
            } else if (var2reg.containsKey(operand1)) {
                reg1 = var2reg.get(operand1);
            } else {
                new MemAsm(AsmOp.LW, reg1, Register.SP, var2Offset.get(operand1));
            }
            if (operand2 instanceof ConstInt constInt) {
                constInt2 = constInt;
            } else if (var2reg.containsKey(operand2)) {
                reg2 = var2reg.get(operand2);
            } else {
                new MemAsm(AsmOp.LW, reg2, Register.SP, var2Offset.get(operand2));
            }

            if ((constInt1 != null && constInt2 == null) || (constInt1 == null && constInt2 != null)) {
                int constNum;
                if (constInt1 == null) {
                    constNum = constInt2.getIntValue();
                } else {
                    constNum = constInt1.getIntValue();
                    reg1 = reg2;
                    asmOp = switch (asmOp) {
                        case BGT -> AsmOp.BLT;
                        case BLT -> AsmOp.BGT;
                        case BLE -> AsmOp.BGE;
                        case BGE -> AsmOp.BLE;
                        default -> asmOp;
                    };
                }
                new BrAsm(currentFunction.getName().substring(1)
                        + "_b" + brInst.getTrueBlock().getName(), reg1, asmOp, constNum);
            } else if (constInt1 != null) {
                new LiAsm(Register.K0, constInt1.getIntValue());
                new BrAsm(currentFunction.getName().substring(1)
                        + "_b" + brInst.getTrueBlock().getName(), Register.K0, asmOp, constInt2.getIntValue());
            } else {
                new BrAsm(currentFunction.getName().substring(1)
                        + "_b" + brInst.getTrueBlock().getName(), reg1, asmOp, reg2);
            }
        }
        new JumpAsm(AsmOp.J, currentFunction.getName().substring(1)
                + "_b" + brInst.getFalseBlock().getName());
    }

    private void buildNoCondBrInst(BrInst brInst) {
        new JumpAsm(AsmOp.J, currentFunction.getName().substring(1)
                + "_b" + brInst.getTrueBlock().getName());
    }

    private void buildCallInst(CallInst callInst) {
        ArrayList<Register> allocatedRegs = new ArrayList<>(
                new HashSet<>(callInst.getActiveReg()));
        ArrayList<MemAsm> lwAssemblies = new ArrayList<>();
        ArrayList<MemAsm> swAssemblies = new ArrayList<>();
        for (Register reg : var2reg.values()) {
            if (reg == Register.A3 || reg == Register.A1 || reg == Register.A2) {
                allocatedRegs.add(reg);
            }
        }
        for (int i = 1; i <= allocatedRegs.size(); i++) {
            swAssemblies.add(new MemAsm(AsmOp.SW, allocatedRegs.get(i - 1),
                    Register.SP, curStackOffset - 4 * i));
        }
        new MemAsm(AsmOp.SW, Register.RA,
                Register.SP, curStackOffset - 4 * allocatedRegs.size() - 4);
        Function calledFunction = callInst.getCalledFunction();
        for (int i = 1; i <= callInst.getOperands().size() - 1; i++) {
            Value param = callInst.getOperands().get(i);
            if (i <= 3) {
                Register paramReg = Register.getByOffset(Register.A0, i);
                if (param instanceof ConstInt constInt) {
                    new LiAsm(paramReg, constInt.getIntValue());
                } else if (var2reg.containsKey(param)) {
                    if (param instanceof FuncParam) {
                        new MemAsm(AsmOp.LW, paramReg, Register.SP,
                                curStackOffset - (allocatedRegs.indexOf(var2reg.get(param)) + 1) * 4);
                    } else {
                        new MoveAsm(paramReg, var2reg.get(param));
                    }
                } else {
                    new MemAsm(AsmOp.LW, paramReg, Register.SP, var2Offset.get(param));
                }
            } else {
                Register paramReg = Register.K0;
                if (param instanceof ConstInt constInt) {
                    new LiAsm(paramReg, constInt.getIntValue());
                } else if (var2reg.containsKey(param)) {
                    if (param instanceof FuncParam) {
                        new MemAsm(AsmOp.LW, paramReg, Register.SP,
                                curStackOffset - (allocatedRegs.indexOf(var2reg.get(param)) + 1) * 4);
                    } else {
                        new MoveAsm(paramReg, var2reg.get(param));
                    }
                } else {
                    new MemAsm(AsmOp.LW, paramReg, Register.SP, var2Offset.get(param));
                }
                new MemAsm(AsmOp.SW, paramReg, Register.SP, curStackOffset - 4 * (allocatedRegs.size() + i + 1));
            }
        }
        new CalcAsm(Register.SP, AsmOp.ADDIU, Register.SP, curStackOffset - 4 * allocatedRegs.size() - 4);
        JumpAsm jalAsm = new JumpAsm(AsmOp.JAL, "func_" + calledFunction.getName().substring(1));
        new MemAsm(AsmOp.LW, Register.RA, Register.SP, 0);
        new CalcAsm(Register.SP, AsmOp.ADDIU, Register.SP, -(curStackOffset - 4 * allocatedRegs.size() - 4));
        for (int i = 1; i <= allocatedRegs.size(); i++) {
            lwAssemblies.add(new MemAsm(AsmOp.LW, allocatedRegs.get(i - 1),
                    Register.SP, curStackOffset - 4 * i));
        }
        jalAsm.setLoadWords(lwAssemblies);
        jalAsm.setStoreWords(swAssemblies);
        if (!calledFunction.getReturnType().equals(IntegerType.VOID)) {
            if (var2reg.containsKey(callInst)) {
                new MoveAsm(var2reg.get(callInst), Register.V0);
            } else {
                new MemAsm(AsmOp.SW, Register.V0, Register.SP, var2Offset.get(callInst));
            }
        }
    }

    private void buildGepInst(GepInst gepInst) {
        Value pointer = gepInst.getPointer();
        Value index = gepInst.getIndex();
        Register pointerReg = Register.K0;
        Register indexReg = Register.K1;
        ValueType type = ((PointerType) pointer.getValueType()).getTargetType();
        if (type instanceof ArrayType arrayType) {
            type = arrayType.getElementType();
        }
        if (pointer instanceof GlobalVar globalVar) {
            new LaAsm(pointerReg, globalVar.getName().substring(1));
        } else if (var2reg.containsKey(pointer)) {
            pointerReg = var2reg.get(pointer);
        } else {
            new MemAsm(AsmOp.LW, pointerReg, Register.SP, var2Offset.get(pointer));
        }
        if (!(index instanceof ConstInt constInt)) {
            if (var2reg.containsKey(index)) {
                indexReg = var2reg.get(index);
            } else {
                new MemAsm(AsmOp.LW, indexReg, Register.SP, var2Offset.get(index));
            }
            new CalcAsm(Register.K1, AsmOp.SLL, indexReg, 2);
            if (var2reg.containsKey(gepInst)) {
                new CalcAsm(var2reg.get(gepInst), AsmOp.ADDU,
                        pointerReg, Register.K1);
            } else {
                new CalcAsm(Register.K0, AsmOp.ADDU,
                        pointerReg, Register.K1);
                new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(gepInst));
            }
            return;
        }
        if (var2reg.containsKey(gepInst)) {
            new CalcAsm(var2reg.get(gepInst), AsmOp.ADDIU,
                    pointerReg, 4 * constInt.getIntValue());
        } else {
            new CalcAsm(Register.K0, AsmOp.ADDIU,
                    pointerReg, 4 * constInt.getIntValue());
            new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(gepInst));
        }
    }

    private void buildLoadInst(LoadInst loadInst) {
        Value pointer = loadInst.getPointer();
        Register pointerReg = Register.K0;
        if (pointer instanceof GlobalVar globalVar) {
            new LaAsm(pointerReg, globalVar.getName().substring(1));
        } else if (var2reg.containsKey(pointer)) {
            pointerReg = var2reg.get(pointer);
        } else {
            new MemAsm(AsmOp.LW, pointerReg, Register.SP, var2Offset.get(pointer));
        }
        if (var2reg.containsKey(loadInst)) {
            new MemAsm(AsmOp.LW, var2reg.get(loadInst), pointerReg, 0);
        } else {
            new MemAsm(AsmOp.LW, Register.K0, pointerReg, 0);
            new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(loadInst));
        }
    }

    private void buildMoveInst(MoveInst moveInst) {
        Value fromValue = moveInst.getFromValue();
        Value toValue = moveInst.getToValue();
        Register reg = Register.K0;
        if (var2reg.containsKey(toValue)) {
            reg = var2reg.get(toValue);
        }
        if (fromValue instanceof ConstInt constInt) {
            new LiAsm(reg, constInt.getIntValue());
        } else if (var2reg.containsKey(fromValue)) {
            new MoveAsm(reg, var2reg.get(fromValue));
        } else {
            new MemAsm(AsmOp.LW, reg, Register.SP, var2Offset.get(fromValue));
        }
        if (reg == Register.K0) {
            new MemAsm(AsmOp.SW, reg, Register.SP, var2Offset.get(toValue));
        }
    }

    private void buildGetintInst(GetintInst getintInst) {
        new LiAsm(Register.V0, 5);
        new SyscallAsm();
        if (var2reg.containsKey(getintInst)) {
            new MoveAsm(var2reg.get(getintInst), Register.V0);
        } else {
            new MemAsm(AsmOp.SW, Register.V0, Register.SP, var2Offset.get(getintInst));
        }
    }

    private void buildGetcharInst(GetcharInst getcharInst) {
        new LiAsm(Register.V0, 12);
        new SyscallAsm();
        if (var2reg.containsKey(getcharInst)) {
            new MoveAsm(var2reg.get(getcharInst), Register.V0);
        } else {
            new MemAsm(AsmOp.SW, Register.V0, Register.SP, var2Offset.get(getcharInst));
        }
    }

    private void buildPutintInst(PutintInst putintInst) {
        Value value = putintInst.getTarget();
        if (value instanceof ConstInt constInt) {
            new LiAsm(Register.A0, constInt.getIntValue());
        } else if (var2reg.containsKey(value)) {
            new MoveAsm(Register.A0, var2reg.get(value));
        } else {
            new MemAsm(AsmOp.LW, Register.A0, Register.SP, var2Offset.get(value));
        }
        new LiAsm(Register.V0, 1);
        new SyscallAsm();
    }

    private void buildPutchInst(PutchInst putchInst) {
        Value value = putchInst.getTarget();
        if (value instanceof ConstInt constInt) {
            new LiAsm(Register.A0, constInt.getIntValue());
        } else if (var2reg.containsKey(value)) {
            new MoveAsm(Register.A0, var2reg.get(value));
        } else {
            int offset = var2Offset.get(value);
            new MemAsm(AsmOp.LW, Register.A0, Register.SP, offset);
        }
        new LiAsm(Register.V0, 11);
        new SyscallAsm();
    }

    private void buildPutstrInst(PutstrInst putstrInst) {
        ConstString constString = putstrInst.getConstString();
        String stringContent = constString.getContent();
        stringContent = stringContent.replace("\\0A", "\n");
        if (stringContent.length() > 1) {
            new LaAsm(Register.A0, "s" + constString.getName().substring(4));
            new LiAsm(Register.V0, 4);
            new SyscallAsm();
        } else {
            new LiAsm(Register.V0, 11);
            for (int i = 0; i < stringContent.length(); i++) {
                char c = stringContent.charAt(i);
                new LiAsm(Register.A0, c);
                new SyscallAsm();
            }
        }
    }

    private void buildRetInst(RetInst retInst) {
        if (isInMain) {
            new LiAsm(Register.V0, 10);
            new SyscallAsm();
        } else {
            Value returnValue = retInst.getReturnValue();
            if (returnValue != null) {
                if (returnValue instanceof ConstInt constInt) {
                    new LiAsm(Register.V0, constInt.getIntValue());
                } else if (var2reg.containsKey(returnValue)) {
                    new MoveAsm(Register.V0, var2reg.get(returnValue));
                } else {
                    new MemAsm(AsmOp.LW, Register.V0, Register.SP, var2Offset.get(returnValue));
                }
            }
            new JumpAsm(AsmOp.JR, Register.RA);
        }
    }

    private void buildStoreInst(StoreInst storeInst) {
        Value pointer = storeInst.getPointer();
        Value storedValue = storeInst.getStoredValue();
        Register reg = Register.K0;
        if (pointer instanceof GlobalVar globalVar) {
            new LaAsm(reg, globalVar.getName().substring(1));
        } else if (var2reg.containsKey(pointer)) {
            reg = var2reg.get(pointer);
        } else {
            new MemAsm(AsmOp.LW, reg, Register.SP, var2Offset.get(pointer));
        }
        if (storedValue instanceof ConstInt constInt) {
            new LiAsm(Register.K1, constInt.getIntValue());
            new MemAsm(AsmOp.SW, Register.K1, reg, 0);
        } else if (var2reg.containsKey(storedValue)) {
            new MemAsm(AsmOp.SW, var2reg.get(storedValue), reg, 0);
        } else {
            new MemAsm(AsmOp.LW, Register.K1, Register.SP, var2Offset.get(storedValue));
            new MemAsm(AsmOp.SW, Register.K1, reg, 0);
        }
    }

    private void buildTruncInst(TruncInst truncInst) {
        Value value = truncInst.getOriginValue();
        ValueType originType = value.getValueType();
        ValueType targetType = truncInst.getValueType();
        Register reg = Register.K0;
        if (value instanceof ConstInt constInt) {
            int after = constInt.getIntValue() & 0xFF;
            if (var2reg.containsKey(truncInst)) {
                if (originType != targetType) {
                    new LiAsm(var2reg.get(truncInst), after);
                } else {
                    new LiAsm(var2reg.get(truncInst), constInt.getIntValue());
                }
            } else {
                if (originType != targetType) {
                    new LiAsm(Register.K0, after);
                } else {
                    new LiAsm(Register.K0, constInt.getIntValue());
                }
                new MemAsm(AsmOp.SW, Register.K0, Register.SP, var2Offset.get(truncInst));
            }
            return;
        }
        if (var2reg.containsKey(value)) {
            reg = var2reg.get(value);
        } else {
            new MemAsm(AsmOp.LW, reg, Register.SP, var2Offset.get(value));
        }
        if (var2reg.containsKey(truncInst)) {
            if (originType != targetType) {
                new CalcAsm(var2reg.get(truncInst), AsmOp.ANDI, reg, 0xFF);
            } else {
                new MoveAsm(var2reg.get(truncInst), reg);
            }
        } else {
            if (originType != targetType) {
                new CalcAsm(Register.K1, AsmOp.ANDI, reg, 0xFF);
                new MemAsm(AsmOp.SW, Register.K1, Register.SP, var2Offset.get(truncInst));
            } else {
                new MemAsm(AsmOp.SW, reg, Register.SP, var2Offset.get(truncInst));
            }
        }
    }

    private void buildZextInst(ZextInst zextInst) {
        Value value = zextInst.getOriginValue();
        Register reg = Register.K0;
        if (value instanceof ConstInt constInt) {
            new LiAsm(reg, constInt.getIntValue());
        } else if (var2reg.containsKey(value)) {
            reg = var2reg.get(value);
        } else {
            new MemAsm(AsmOp.LW, reg, Register.SP, var2Offset.get(value));
        }
        if (var2reg.containsKey(zextInst)) {
            new MoveAsm(var2reg.get(zextInst), reg);
        } else {
            new MemAsm(AsmOp.SW, reg, Register.SP, var2Offset.get(zextInst));
        }
    }

}
