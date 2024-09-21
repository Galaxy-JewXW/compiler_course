package tools;

import frontend.token.TokenType;
import middle.component.BasicBlock;
import middle.component.ConstArray;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.instructions.AllocInst;
import middle.component.instructions.BinaryInst;
import middle.component.instructions.BrInst;
import middle.component.instructions.CallInst;
import middle.component.instructions.GepInst;
import middle.component.instructions.LoadInst;
import middle.component.instructions.OperatorType;
import middle.component.instructions.RetInst;
import middle.component.instructions.StoreInst;
import middle.component.instructions.TruncInst;
import middle.component.instructions.ZextInst;
import middle.component.model.Value;
import middle.component.types.ArrayType;
import middle.component.types.FunctionType;
import middle.component.types.IntegerType;
import middle.component.types.PointerType;
import middle.component.types.ValueType;

import java.util.ArrayList;
import java.util.Collections;

public class Builder {
    public static int calculate(int a, int b, TokenType op) {
        return switch (op) {
            case PLUS -> a + b;
            case MINU -> a - b;
            case MULT -> a * b;
            case DIV -> a / b;
            case MOD -> a % b;
            default -> throw new RuntimeException("Unexpected token type: " + op);
        };
    }

    public static GlobalVar buildGlobalVar(String name, ValueType valueType,
                                           Value initValue, boolean isConstant) {
        return new GlobalVar(name, valueType, initValue, isConstant);
    }

    public static AllocInst buildVar(ValueType valueType, Value initValue, BasicBlock basicBlock) {
        AllocInst allocInst = new AllocInst(valueType, basicBlock);
        Value tempInitValue = initValue;
        if (initValue != null) {
            if (valueType.equals(IntegerType.i8)
                    && initValue.getValueType().equals(IntegerType.i32)) {
                if (initValue instanceof ConstInt constInt) {
                    // 对于char c = 2的情况，直接将2截断处理即可。
                    ConstInt newInt = new ConstInt(
                            constInt.getIntValue() & 0xFF, IntegerType.i8);
                    buildStoreInst(newInt, allocInst, basicBlock);
                    return allocInst;
                }
                tempInitValue = buildTruncInst(initValue, IntegerType.i8, basicBlock);
            }
            buildStoreInst(tempInitValue, allocInst, basicBlock);
        }
        return allocInst;
    }

    public static GlobalVar buildGlobalArray(String name, ValueType type,
                                             Value initValue, boolean isConstant) {
        ConstArray constArray = (ConstArray) initValue;
        if (initValue == null && type instanceof ArrayType arrayType) {
            constArray = new ConstArray(arrayType, arrayType.getLength());
        }
        return new GlobalVar(name, type, constArray, isConstant);
    }

    public static AllocInst buildArray(ValueType valueType, Value initValue,
                                       BasicBlock basicBlock) {
        AllocInst allocInst = new AllocInst(valueType, basicBlock);
        if (initValue != null) {
            if (initValue instanceof ConstArray constArray) {
                for (int i = 0; i < constArray.getFilled(); i++) {
                    Value value = constArray.getElements().get(i);
                    ConstInt index = new ConstInt(i, IntegerType.i32);
                    ArrayList<Value> constInts = new ArrayList<>();
                    constInts.add(ConstInt.i32ZERO);
                    constInts.add(index);
                    buildStoreInst(value, buildGEPInst(allocInst, constInts, basicBlock), basicBlock);
                }
            }
        }
        return allocInst;
    }

    public static GepInst buildGEPInst(Value pointerBase, ArrayList<Value> indexes,
                                       BasicBlock basicBlock) {
        return new GepInst(pointerBase, indexes, basicBlock);
    }

    public static BinaryInst buildBinaryInst(Value lValue, OperatorType op,
                                             Value rValue, BasicBlock basicBlock) {
        // 这里的的格式为 x = op a b, 填入的lValue和rValue分别对应a, b
        Value lVal1 = lValue;
        Value rVal1 = rValue;
        if (lValue.getValueType() instanceof IntegerType integerType
                && integerType.equals(IntegerType.i32)) {
            if (rValue.getValueType() instanceof IntegerType integerType1
                    && !integerType1.equals(IntegerType.i32)) {
                rVal1 = buildZextInst(rVal1, IntegerType.i32, basicBlock);
            }
        } else if (rValue.getValueType() instanceof IntegerType integerType
                && integerType.equals(IntegerType.i32)) {
            if (lVal1.getValueType() instanceof IntegerType integerType1
                    && !integerType1.equals(IntegerType.i32)) {
                lVal1 = buildZextInst(lVal1, IntegerType.i32, basicBlock);
            }
        }
        return new BinaryInst(basicBlock, op, lVal1, rVal1);
    }

    public static ZextInst buildZextInst(Value value, ValueType targetType, BasicBlock basicBlock) {
        return new ZextInst(value, basicBlock, targetType);
    }

    public static TruncInst buildTruncInst(Value value, ValueType targetType, BasicBlock basicBlock) {
        return new TruncInst(value, basicBlock, targetType);
    }

    public static LoadInst buildLoadInst(Value pointer, BasicBlock basicBlock) {
        return new LoadInst(basicBlock, pointer);
    }

    public static StoreInst buildStoreInst(Value value, Value pointer, BasicBlock basicBlock) {
        Value tempValue = value;
        if (((PointerType) pointer.getValueType()).getTargetType().equals(IntegerType.i8)
                && value.getValueType().equals(IntegerType.i32)) {
            tempValue = buildTruncInst(value, IntegerType.i8, basicBlock);
        }
        if (((PointerType) pointer.getValueType()).getTargetType().equals(IntegerType.i32)
                && value.getValueType().equals(IntegerType.i8)) {
            tempValue = buildZextInst(value, IntegerType.i32, basicBlock);
        }
        return new StoreInst(basicBlock, tempValue, pointer);
    }

    public static FunctionType buildFunctionType(ValueType retType, ArrayList<ValueType> paramsType) {
        return new FunctionType(retType, paramsType);
    }

    public static Function buildFunction(String name, ValueType returnType,
                                         ArrayList<ValueType> argumentsType) {
        return new Function(name, new FunctionType(returnType, argumentsType), false);
    }

    public static Function buildBuiltInFunc(String name, ValueType retType, ArrayList<ValueType> params) {
        return new Function(name, new FunctionType(retType, params), true);
    }

    public static Function buildBuiltInFunc(String name, ValueType retType, ValueType paramType) {
        ArrayList<ValueType> params = new ArrayList<>(Collections.singleton(paramType));
        return buildBuiltInFunc(name, retType, params);
    }

    public static CallInst buildCallInst(Function function, ArrayList<Value> arguments,
                                         BasicBlock basicBlock) {
        return new CallInst(basicBlock, function, arguments);
    }

    public static BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public static BasicBlock buildUnnamedBasicBlock() {
        return new BasicBlock();
    }

    public static BrInst buildBrInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        if (!basicBlock.isTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            trueBlock.addPrevBlock(basicBlock);
        }
        return new BrInst(basicBlock, trueBlock);
    }

    public static BrInst buildBrInst(BasicBlock basicBlock, Value cond,
                                     BasicBlock trueBlock, BasicBlock falseBlock) {
        if (!basicBlock.isTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            trueBlock.addPrevBlock(basicBlock);
            basicBlock.addNextBlock(falseBlock);
            falseBlock.addPrevBlock(basicBlock);
        }
        return new BrInst(basicBlock, trueBlock, falseBlock, cond);
    }

    public static ConstInt buildConstInt(int val, ValueType intType) {
        int value = val;
        if (intType.equals(IntegerType.i8)) {
            value = value & 0xFF;
        }
        return new ConstInt(value, intType);
    }

    public static RetInst buildRetInst(BasicBlock basicBlock) {
        return new RetInst(basicBlock);
    }

    public static RetInst buildRetInst(BasicBlock basicBlock, Value returnValue) {
        return new RetInst(basicBlock, returnValue);
    }
}
