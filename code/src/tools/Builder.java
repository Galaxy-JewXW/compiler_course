package tools;

import frontend.token.TokenType;
import middle.*;
import middle.instructions.*;
import middle.model.Value;
import middle.types.*;

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
            if (valueType.equals(IntegerType.i8)) {
                if (initValue.getValueType().equals(IntegerType.i32)) {
                    tempInitValue = buildTruncInst(initValue, IntegerType.i8, basicBlock);
                }
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
            ArrayList<Value> indexes = new ArrayList<>();
            indexes.add(ConstInt.i32ZERO);
            if (initValue instanceof Assignable) {
                buildStoreInst(initValue, buildGEPInst(allocInst,
                        new ArrayList<>(indexes), basicBlock), basicBlock);
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

    public static StoreInst buildStoreInst(Value value, Value pointer, BasicBlock basicBlock) {
        return new StoreInst(basicBlock, value, pointer);
    }

    public static FunctionType buildFunctionType(ValueType retType, ArrayList<ValueType> paramsType) {
        return new FunctionType(retType, paramsType);
    }

    public static Function buildFunction(String name, FunctionType functionType) {
        return new Function(name, functionType, false);
    }

    public static Function buildBuiltInFunc(String name, ValueType retType, ArrayList<ValueType> params) {
        FunctionType FunctionType = new FunctionType(retType, params);
        return new Function(name, FunctionType, true);
    }

    public static Function buildBuiltInFunc(String name, ValueType retType, ValueType paramType) {
        ArrayList<ValueType> params = new ArrayList<>(Collections.singleton(paramType));
        return buildBuiltInFunc(name, retType, params);
    }

    public static BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public static BasicBlock buildUnnamedBasicBlock() {
        return new BasicBlock();
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
