package tools;

import frontend.token.TokenType;
import middle.BasicBlock;
import middle.ConstInt;
import middle.Function;
import middle.instructions.RetInst;
import middle.model.Value;
import middle.types.FunctionType;
import middle.types.IntegerType;
import middle.types.ValueType;

import java.util.ArrayList;
import java.util.Collections;

public class InstBuilder {
    public int calculate(int a, int b, TokenType op) {
        return switch (op) {
            case PLUS -> a + b;
            case MINU -> a - b;
            case MULT -> a * b;
            case DIV -> a / b;
            case MOD -> a % b;
            default -> throw new RuntimeException("Unexpected token type: " + op);
        };
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
