package llvm;

import llvm.types.FuncType;
import llvm.types.IntegerType;
import llvm.types.Type;
import llvm.values.*;
import llvm.values.instructions.*;
import llvm.values.instructions.BrInst;
import llvm.values.instructions.RetInst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


public class InstrBuilder {

    public static FuncType buildFuncType(Type retType, ArrayList<Type> paramsType) {
        return new FuncType(retType, paramsType);
    }

    public static Function buildFunction(String name, FuncType funcType) {
        return new Function(name, funcType, false);
    }

    public static Function buildBuiltInFunc(String name, Type retType, ArrayList<Type> params) {
        FuncType funcType = new FuncType(retType, params);
        return new Function(name, funcType, true);
    }

    public static Function buildBuiltInFunc(String name, Type retType, Type paramType) {
        ArrayList<Type> params = new ArrayList<>(Collections.singleton(paramType));
        return buildBuiltInFunc(name, retType, params);
    }

    public static BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public static BasicBlock buildUnnamedBasicBlock() {
        return new BasicBlock();
    }

    public static RetInst buildRetInst(BasicBlock basicBlock) {
        return new RetInst(basicBlock);
    }

    public static RetInst buildRetInst(BasicBlock basicBlock, Value value) {
        return new RetInst(basicBlock, value);
    }

    public static ConstInt buildConstInt(int val) {
        return new ConstInt(val);
    }

    public static GlobalVar buildGlobalVar(String name, Type type, boolean isConstant, Value value) {
        return new GlobalVar(name, type, isConstant, value);
    }

    public static AllocInst buildVar(Type type, Value value, BasicBlock basicBlock) {
        AllocInst allocInst = new AllocInst(type, basicBlock);
        if (value != null) {
            buildStoreInst(basicBlock, value, allocInst);
        }
        return allocInst;
    }

    public static StoreInst buildStoreInst(BasicBlock basicBlock, Value val, Value ptr) {
        return new StoreInst(basicBlock, val, ptr);
    }

    public static BinaryInst buildBinaryInst(BasicBlock basicBlock, Operator op, Value lVal, Value rVal) {
        Value lVal1 = lVal;
        Value rVal1 = rVal;
        if ((lVal.getType() instanceof IntegerType integerType1) && integerType1 == IntegerType.i1
                && (rVal.getType() instanceof IntegerType integerType2) && integerType2 == IntegerType.i32) {
            lVal1 = InstrBuilder.buildZextInst(basicBlock, lVal1);
        } else if ((lVal.getType() instanceof IntegerType integerType1) && integerType1 == IntegerType.i32
                && (rVal.getType() instanceof IntegerType integerType2) && integerType2 == IntegerType.i1) {
            rVal1 = InstrBuilder.buildZextInst(basicBlock, rVal1);
        }
        return new BinaryInst(basicBlock, op, lVal1, rVal1);
    }

    public static LoadInst buildLoadInst(BasicBlock basicBlock, Value pointer) {
        return new LoadInst(basicBlock, pointer);
    }

    public static CallInst buildCallInst(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        return new CallInst(basicBlock, function, args);
    }

    public static BrInst buildBrInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        if (!basicBlock.isTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            trueBlock.addPrevBlock(basicBlock);
        }
        return new BrInst(basicBlock, trueBlock);
    }

    public static BrInst buildBrInst(BasicBlock basicBlock, BasicBlock trueBlock,
                              BasicBlock falseBlock, Value cond) {
        if (!basicBlock.isTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            basicBlock.addNextBlock(falseBlock);
            trueBlock.addPrevBlock(basicBlock);
            falseBlock.addPrevBlock(basicBlock);
        }
        return new BrInst(basicBlock, trueBlock, falseBlock, cond);
    }

    public static ZextInst buildZextInst(BasicBlock basicBlock, Value value) {
        return new ZextInst(Operator.ZEXT, value, basicBlock);
    }

    public static GlobalVar buildGlobalArray(String name, Type type, boolean isConstant, Value initValue) {
        ConstArray constArray = (ConstArray) initValue;
        if (initValue == null) {
            constArray = new ConstArray(type);
        }
        return new GlobalVar(name, type, isConstant, constArray);
    }

    public static AllocInst buildArray(Type type, Value initVal, BasicBlock basicBlock) {
        AllocInst res = new AllocInst(type, basicBlock);
        if (initVal != null) {
            Stack<Value> indices = new Stack<>();
            setArrayInitVal(res, initVal, basicBlock, indices, 0);
        }
        return res;
    }

    public static void setArrayInitVal(Value pointer, Value initVal, BasicBlock basicBlock,
                                       Stack<Value> indexes, int off) {
        indexes.push(new ConstInt(off));
        if (initVal instanceof Assignable) {
            if (initVal instanceof ConstArray constArray) {
                int tmp = 0;
                for (Value value : constArray.getValues()) {
                    setArrayInitVal(pointer, value, basicBlock, indexes, tmp++);
                }
            } else {
                buildStoreInst(basicBlock, initVal, buildGEPInst(pointer, new ArrayList<>(indexes), basicBlock));
            }
        }
        indexes.pop();
    }

    public static GEPInst buildGEPInst(Value base, ArrayList<Value> indices, BasicBlock basicBlock) {
        return new GEPInst(base, indices, basicBlock);
    }
}
