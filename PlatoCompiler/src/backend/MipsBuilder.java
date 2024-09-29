package backend;

import backend.enums.AsmOp;
import backend.enums.Register;
import backend.global.Asciiz;
import backend.global.Word;
import backend.text.*;
import middle.component.Module;
import middle.component.*;
import middle.component.instruction.*;
import middle.component.instruction.io.GetintInst;
import middle.component.instruction.io.PutintInst;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsBuilder {
    private final Module module;
    private int curStackOffset;
    private HashMap<Value, Integer> stackOffsetMap;
    private HashMap<Value, Register> var2reg;
    private boolean isInMain = false;
    private Function currentFunction;
    private HashMap<Value, Integer> var2Offset;

    public MipsBuilder(Module module) {
        this.module = module;
    }

    public void build() {
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
    }

    private void buildConstString(ConstString constString) {
        new Asciiz("s" + constString.getName().substring(4),
                constString.getContent());
    }

    private void buildGlobalVar(GlobalVar globalVar) {
        ValueType targetType = ((PointerType) globalVar.getValueType())
                .getTargetType();
        if (targetType.equals(IntegerType.i32) || targetType.equals(IntegerType.i8)) {
            if (globalVar.getInitialValue().getElements() == null) {
                new Word(globalVar.getName().substring(1), 0);
            } else {
                new Word(globalVar.getName().substring(1),
                        globalVar.getInitialValue().getElements().get(0));
            }
        } else {
            // 初始化全局数组
            ArrayList<Integer> list = globalVar.getInitialValue().getElements();
            int length = globalVar.getInitialValue().getLength();
            new Word(globalVar.getName().substring(1), list, length);
        }
    }

    private void buildFunction(Function function) {
        currentFunction = function;
        var2Offset = new HashMap<>();
        curStackOffset = 0;
        var2reg = new HashMap<>(function.getVar2reg());
        new Label(function.getName().substring(1), true);
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
                        var2Offset.put(instruction, curStackOffset);
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
                + "_b" + block.getName(), false);
        for (Instruction instruction : block.getInstructions()) {
            new Comment("# " + instruction.toString());
            buildInstruction(instruction);
        }
    }

    private void buildInstruction(Instruction instruction) {
        if (instruction instanceof AllocInst allocInst) {
            buildAllocInst(allocInst);
        } else if (instruction instanceof BinaryInst binaryInst) {
            if (OperatorType.isLogicalOperator(binaryInst.getOpType())) {
                // TODO
            } else {
                buildBinaryInst(binaryInst);
            }
        } else if (instruction instanceof GetintInst getintInst) {
            buildGetintInst(getintInst);
        } else if (instruction instanceof PutintInst putintInst) {
            buildPutintInst(putintInst);
        } else if (instruction instanceof RetInst retInst) {
            buildRetInst(retInst);
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
                new MDAsm(Register.K0, AsmOp.DIV, Register.K1);
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
            }
        }
    }

    private void makeVarMulConst(Register varReg, int constInt, Register targetReg) {
        int bitCnt = Integer.bitCount(constInt);
        if (constInt < 0 || bitCnt > 3) {
            new LiAsm(Register.V0, constInt);
            new CalcAsm(targetReg, AsmOp.MUL, varReg, Register.V0);
        }
    }

    private void buildGetintInst(GetintInst getintInst) {
        new LiAsm(Register.V0, 5);
        new SyscallAsm();
        if (var2reg.containsKey(getintInst)) {
            new CalcAsm(var2reg.get(getintInst), AsmOp.ADDIU, Register.V0, 0);
        } else {
            new MemAsm(AsmOp.SW, Register.V0, Register.SP, var2Offset.get(getintInst));
        }
    }

    private void buildPutintInst(PutintInst putintInst) {
        Value value = putintInst.getTarget();
        if (value instanceof ConstInt constInt) {
            new LiAsm(Register.A0, constInt.getIntValue());
        } else if (var2reg.containsKey(value)) {
            new CalcAsm(Register.A0, AsmOp.ADDIU, var2reg.get(value), 0);
        } else {
            int offset = var2Offset.get(value);
            new MemAsm(AsmOp.LW, Register.A0, Register.SP, offset);
        }
        new LiAsm(Register.V0, 1);
        new SyscallAsm();
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
                    new CalcAsm(Register.V0, AsmOp.ADDIU, var2reg.get(returnValue), 0);
                } else {
                    new MemAsm(AsmOp.LW, Register.V0, Register.SP, var2Offset.get(retInst));
                }
            }
            new JumpAsm(AsmOp.JR, Register.RA);
        }
    }


}
