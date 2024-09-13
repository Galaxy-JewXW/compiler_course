package llvm.values;

import llvm.types.FunctionType;
import llvm.types.IntType;
import llvm.types.ValueType;
import llvm.values.instructions.AllocInstruction;
import llvm.values.instructions.BinaryInstruction;
import llvm.values.instructions.BrInstruction;
import llvm.values.instructions.CallInstruction;
import llvm.values.instructions.GEPInstruction;
import llvm.values.instructions.LoadInstruction;
import llvm.values.instructions.Operator;
import llvm.values.instructions.RetInstruction;
import llvm.values.instructions.StoreInstruction;
import llvm.values.instructions.ZextInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Builder {
    public static FunctionType buildFunctionType(ValueType returnType,
                                                 ArrayList<ValueType> paramsType) {
        return new FunctionType(returnType, paramsType);
    }

    public static Function buildFunction(String name, FunctionType functionType) {
        return new Function(name, functionType, false);
    }

    public static Function buildBulitInFunction(String name, ValueType returnType,
                                                ArrayList<ValueType> paramsType) {
        return new Function(name, new FunctionType(returnType, paramsType), true);
    }

    public static Function buildBulitInFunction(String name, ValueType returnType,
                                                ValueType paramType) {
        ArrayList<ValueType> paramsType = new ArrayList<>(Collections.singleton(paramType));
        return new Function(name, new FunctionType(returnType, paramsType), true);
    }

    public static BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public static BasicBlock buildUnnamedBasicBlock() {
        return new BasicBlock();
    }

    public static RetInstruction buildRetInstruction(BasicBlock basicBlock) {
        return new RetInstruction(basicBlock);
    }

    public static RetInstruction buildRetInstruction(BasicBlock basicBlock, Value value) {
        return new RetInstruction(basicBlock, value);
    }

    public static ConstInt buildConstInt(int value) {
        return new ConstInt(value);
    }

    public static GlobalVar buildGlobalVar(String name, ValueType type, boolean isCon, Value value) {
        return new GlobalVar(name, type, isCon, value);
    }

    public static AllocInstruction buildVar(ValueType type, Value value, BasicBlock basicBlock) {
        AllocInstruction allocInstruction = new AllocInstruction(type, basicBlock);
        if (value != null) {
            buildStoreInstruction(basicBlock, value, allocInstruction);
        }
        return allocInstruction;
    }

    public static StoreInstruction buildStoreInstruction(BasicBlock basicBlock, Value val, Value ptr) {
        return new StoreInstruction(basicBlock, val, ptr);
    }

    public static BinaryInstruction buildBinaryInstruction(BasicBlock basicBlock, Operator op,
                                                           Value lVal, Value rVal) {
        final boolean isLi1 = (lVal.getType() instanceof IntType intType) && intType.getBits() == 1;
        final boolean isRi1 = (rVal.getType() instanceof IntType intType) && intType.getBits() == 1;
        final boolean isLi32 = (lVal.getType() instanceof IntType intType) && intType.getBits() == 32;
        final boolean isRi32 = (rVal.getType() instanceof IntType intType) && intType.getBits() == 32;
        Value lVal1 = lVal;
        Value rVal1 = rVal;
        if (isLi1 && isRi32) {
            lVal1 = Builder.buildZextInstruction(basicBlock, lVal1);
        } else if (isLi32 && isRi1) {
            rVal1 = Builder.buildZextInstruction(basicBlock, rVal1);
        }
        return new BinaryInstruction(basicBlock, op, lVal1, rVal1);
    }

    public static LoadInstruction buildLoadInstruction(BasicBlock basicBlock, Value pointer) {
        return new LoadInstruction(basicBlock, pointer);
    }

    public static CallInstruction buildCallInstruction(BasicBlock basicBlock, Function function,
                                                       ArrayList<Value> arguments) {
        return new CallInstruction(basicBlock, function, arguments);
    }

    public static BrInstruction buildBrInstruction(BasicBlock basicBlock, BasicBlock trueBlock) {
        if (basicBlock.notTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            trueBlock.addPrevBlock(basicBlock);
        }
        return new BrInstruction(basicBlock, trueBlock);
    }

    public static BrInstruction buildBrInstruction(BasicBlock basicBlock, BasicBlock trueBlock,
                         BasicBlock falseBlock, Value cond) {
        if (basicBlock.notTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            basicBlock.addNextBlock(falseBlock);
            trueBlock.addPrevBlock(basicBlock);
            falseBlock.addPrevBlock(basicBlock);
        }
        return new BrInstruction(basicBlock, trueBlock, falseBlock, cond);
    }

    public static ZextInstruction buildZextInstruction(BasicBlock basicBlock, Value value) {
        return new ZextInstruction(Operator.ZEXT, value, basicBlock);
    }

    public static GlobalVar buildGlobalArray(String name, ValueType type, boolean isConstant, Value initValue) {
        ConstArray constArray = (ConstArray) initValue;
        if (initValue == null) {
            constArray = new ConstArray(type);
        }
        return new GlobalVar(name, type, isConstant, constArray);
    }

    public static AllocInstruction buildArray(ValueType type, Value initVal, BasicBlock basicBlock) {
        AllocInstruction allocInstruction = new AllocInstruction(type, basicBlock);
        if (initVal != null) {
            Stack<Value> indexes = new Stack<>();
            setArrayInitVal(allocInstruction, initVal, basicBlock, indexes, 0);
        }
        return allocInstruction;
    }

    private static void setArrayInitVal(Value pointer, Value initVal,
                                        BasicBlock basicBlock, Stack<Value> indexes, int off) {
        indexes.push(new ConstInt(off));
        if (initVal instanceof Assignable) {
            if (initVal instanceof ConstArray constArray) {
                int tmp = 0;
                for (Value value : constArray.getValues()) {
                    setArrayInitVal(pointer, value, basicBlock, indexes, tmp++);
                }
            } else {
                buildStoreInstruction(basicBlock, initVal, buildGEPInstruction(pointer,
                        new ArrayList<>(indexes), basicBlock));
            }
        }
        indexes.pop();
    }

    public static GEPInstruction buildGEPInstruction(Value base, ArrayList<Value> indexes, BasicBlock basicBlock) {
        return new GEPInstruction(base, indexes, basicBlock);
    }
}
