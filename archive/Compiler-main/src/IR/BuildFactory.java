package IR;

import IR.types.FuncType;
import IR.types.IntegerType;
import IR.types.Type;
import IR.values.*;
import IR.values.instructions.*;
import IR.values.instructions.men.*;
import IR.values.instructions.terminator.BrInst;
import IR.values.instructions.terminator.RetInst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


public class BuildFactory {
    private static final BuildFactory buildFactory = new BuildFactory();

    public static BuildFactory getInstance() {
        return buildFactory;
    }

    public FuncType buildFuncType(Type retType, ArrayList<Type> paramsType) {
        return new FuncType(retType, paramsType);
    }

    public Function buildFunction(String name, FuncType funcType) {
        return new Function(name, funcType, false);
    }

    public Function buildLibFunc(String name, Type retType, ArrayList<Type> params) {
        FuncType funcType = new FuncType(retType, params);
        return new Function(name, funcType, true);
    }

    public Function buildOneParamLibFunc(String name, Type retType, Type paramType) {
        ArrayList<Type> params = new ArrayList<>(Collections.singleton(paramType));
        return buildLibFunc(name, retType, params);
    }

    public BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public BasicBlock unnamedBasicBlock() {
        return new BasicBlock();
    }

    public RetInst buildRetInst(BasicBlock basicBlock) {
        return new RetInst(basicBlock);
    }

    public RetInst buildRetInst(BasicBlock basicBlock, Value value) {
        return new RetInst(basicBlock, value);
    }

    public ConstInt buildConstInt(int val) {
        return new ConstInt(val);
    }

    public GlobalVar globalVar(String name, Type type, boolean isCon, Value value) {
        return new GlobalVar(name, type, isCon, value);
    }

    public AllocInst buildVar(Type type, Value value, BasicBlock basicBlock) {
        AllocInst allocInst = new AllocInst(type, basicBlock);
        if (value != null) {
            storeInst(basicBlock, value, allocInst);
        }
        return allocInst;
    }

    public StoreInst storeInst(BasicBlock basicBlock, Value val, Value ptr) {
        return new StoreInst(basicBlock, val, ptr);
    }

    public BinaryInst binaryInst(BasicBlock basicBlock, Operator op, Value lVal, Value rVal) {
        boolean isLi1 = (lVal.getType() instanceof IntegerType integerType) && integerType.isI1();
        boolean isRi1 = (rVal.getType() instanceof IntegerType integerType) && integerType.isI1();
        boolean isLi32 = (lVal.getType() instanceof IntegerType integerType) && integerType.isI32();
        boolean isRi32 = (rVal.getType() instanceof IntegerType integerType) && integerType.isI32();
        Value LVAL = lVal;
        Value RVAL = rVal;
        if (isLi1 && isRi32) {
            LVAL = BuildFactory.getInstance().buildZext(basicBlock, LVAL);
        } else if (isLi32 && isRi1) {
            RVAL = BuildFactory.getInstance().buildZext(basicBlock, RVAL);
        }
        return new BinaryInst(basicBlock, op, LVAL, RVAL);
    }

    public LoadInst loadInst(BasicBlock basicBlock, Value pointer) {
        return new LoadInst(basicBlock, pointer);
    }

    public CallInst callInst(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        return new CallInst(basicBlock, function, args);
    }

    public BrInst brInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        if (!basicBlock.isTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            trueBlock.addPrevBlock(basicBlock);
        }
        return new BrInst(basicBlock, trueBlock);
    }

    public BrInst brInst(BasicBlock basicBlock, BasicBlock trueBlock,
                         BasicBlock falseBlock, Value cond) {
        if (!basicBlock.isTerminated()) {
            basicBlock.addNextBlock(trueBlock);
            basicBlock.addNextBlock(falseBlock);
            trueBlock.addPrevBlock(basicBlock);
            falseBlock.addPrevBlock(basicBlock);
        }
        return new BrInst(basicBlock, trueBlock, falseBlock, cond);
    }

    public ConvInst buildZext(BasicBlock basicBlock, Value value) {
        return new ConvInst(Operator.Zext, value, basicBlock);
    }

    public GlobalVar buildGlobalArray(String name, Type type, boolean isCon, Value initValue) {
        ConstArray constArray = (ConstArray) initValue;
        if (initValue == null) {
            constArray = new ConstArray(type);
        }
        return new GlobalVar(name, type, isCon, constArray);
    }

    public AllocInst buildArray(Type type, Value initVal, BasicBlock basicBlock) {
        AllocInst res = new AllocInst(type, basicBlock);
        if (initVal != null) {
            Stack<Value> indices = new Stack<>();
            setArrayInitVal(res, initVal, basicBlock, indices, 0);
        }
        return res;
    }

    private void setArrayInitVal(Value pointer, Value initVal, BasicBlock basicBlock, Stack<Value> indices, int off) {
        indices.push(new ConstInt(off));
        if (initVal instanceof Assignable) {
            if (initVal instanceof ConstArray constArray) {
                int tmp = 0;
                for (Value value : constArray.getValues()) {
                    setArrayInitVal(pointer, value, basicBlock, indices, tmp++);
                }
            } else {
                storeInst(basicBlock, initVal, gepInst(pointer, new ArrayList<>(indices), basicBlock));
            }
        }
        indices.pop();
    }

    public GEPInst gepInst(Value base, ArrayList<Value> indices, BasicBlock basicBlock) {
        return new GEPInst(base, indices, basicBlock);
    }
}
