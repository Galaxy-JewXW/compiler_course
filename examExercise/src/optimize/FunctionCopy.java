package optimize;

import middle.IRData;
import middle.component.*;
import middle.component.instruction.*;
import middle.component.instruction.io.*;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiFunction;

public class FunctionCopy {
    private static final HashMap<Value, Value> valueMap = new HashMap<>();
    private static final HashSet<BasicBlock> visitedBlocks = new HashSet<>();
    private static final HashSet<PhiInst> phiInstructions = new HashSet<>();
    private static final HashSet<Instruction> clonedInstructions = new HashSet<>();

    // 定义指令类型与克隆逻辑的映射
    private static final Map<Class<? extends Instruction>,
            BiFunction<Instruction, BasicBlock, Instruction>> cloneMap = new HashMap<>();

    static {
        // 填充映射表，将每种指令类型与其克隆逻辑绑定
        cloneMap.put(GetintInst.class, (inst, parentBlock) -> new GetintInst());
        cloneMap.put(GetcharInst.class, (inst, parentBlock) -> new GetcharInst());
        cloneMap.put(PutintInst.class, (inst, parentBlock) -> new PutintInst(getOrCreate(((PutintInst) inst).getTarget())));
        cloneMap.put(PutchInst.class, (inst, parentBlock) -> new PutchInst(getOrCreate(((PutchInst) inst).getTarget())));
        cloneMap.put(PutstrInst.class, (inst, parentBlock) -> new PutstrInst(((PutstrInst) inst).getConstString()));
        cloneMap.put(AllocInst.class, (inst, parentBlock) -> new AllocInst(((AllocInst) inst).getTargetType()));
        cloneMap.put(BinaryInst.class, (inst, parentBlock) -> new BinaryInst(
                inst.getOpType(),
                getOrCreate(((BinaryInst) inst).getOperand1()),
                getOrCreate(((BinaryInst) inst).getOperand2())));
        cloneMap.put(BrInst.class, (inst, parentBlock) -> cloneBranchInst((BrInst) inst, parentBlock));
        cloneMap.put(GepInst.class, (inst, parentBlock) -> new GepInst(
                getOrCreate(((GepInst) inst).getPointer()),
                getOrCreate(((GepInst) inst).getIndex())));
        cloneMap.put(LoadInst.class, (inst, parentBlock) -> new LoadInst(getOrCreate(((LoadInst) inst).getPointer())));
        cloneMap.put(PhiInst.class, (inst, parentBlock) -> clonePhiInst((PhiInst) inst, parentBlock));
        cloneMap.put(RetInst.class, (inst, parentBlock) -> new RetInst(getOrCreate(((RetInst) inst).getReturnValue())));
        cloneMap.put(StoreInst.class, (inst, parentBlock) -> new StoreInst(
                getOrCreate(((StoreInst) inst).getPointer()),
                getOrCreate(((StoreInst) inst).getStoredValue())));
        cloneMap.put(ZextInst.class, (inst, parentBlock) -> new ZextInst(
                getOrCreate(((ZextInst) inst).getOriginValue()),
                inst.getValueType()));
        cloneMap.put(TruncInst.class, (inst, parentBlock) -> new TruncInst(
                getOrCreate(((TruncInst) inst).getOriginValue()),
                inst.getValueType()));
    }

    public static Function copyFunction(Function originalFunc) {
        visitedBlocks.clear();
        phiInstructions.clear();
        clonedInstructions.clear();
        valueMap.clear();
        Function clonedFunc = createClonedFunctionSkeleton(originalFunc);
        for (BasicBlock block : originalFunc.getBasicBlocks()) {
            BasicBlock clonedBlock = new BasicBlock(IRData.getBlockName());
            clonedBlock.setFunction(clonedFunc);
            clonedFunc.addBasicBlock(clonedBlock);
            valueMap.put(block, clonedBlock);
        }
        cloneBlockContents(originalFunc.getEntryBlock());
        for (PhiInst phi : phiInstructions) {
            PhiInst clonedPhi = (PhiInst) getOrCreate(phi);
            for (int i = 0; i < phi.getOperands().size(); i++) {
                clonedPhi.addValue(
                        (BasicBlock) getOrCreate(phi.getBlocks().get(i)),
                        getOrCreate(phi.getOperands().get(i))
                );
            }
        }
        return clonedFunc;
    }

    private static Function createClonedFunctionSkeleton(Function originalFunc) {
        Function clonedFunc = new Function(originalFunc.getName() + "__copied",
                originalFunc.getReturnType(), false);
        for (FuncParam param : originalFunc.getFuncParams()) {
            FuncParam clonedParam = new FuncParam(IRData.getVarName(), param.getValueType());
            valueMap.put(param, clonedParam);
            clonedFunc.addFuncParam(clonedParam);
        }
        return clonedFunc;
    }

    /**
     * 递归地克隆基本块及其子块的内容
     *
     * @param originalBlock 要克隆的原始基本块
     */
    private static void cloneBlockContents(BasicBlock originalBlock) {
        visitedBlocks.add(originalBlock);
        for (Instruction instruction : originalBlock.getInstructions()) {
            cloneInstruction(instruction, originalBlock, false);
        }
        for (BasicBlock childBlock : originalBlock.getNextBlocks()) {
            if (!visitedBlocks.contains(childBlock)) {
                cloneBlockContents(childBlock);
            }
        }
    }

    /**
     * 获取或创建一个值的克隆
     *
     * @param originalValue 原始值
     * @return 克隆后的值
     */
    public static Value getOrCreate(Value originalValue) {
        if (originalValue == null) {
            return null;
        }
        // 常量、函数和全局变量不需要克隆
        if (originalValue instanceof ConstInt || originalValue instanceof Function ||
                originalValue instanceof GlobalVar) {
            return originalValue;
        }
        // 已经克隆过
        if (valueMap.containsKey(originalValue)) {
            return valueMap.get(originalValue);
        }
        if (originalValue instanceof BasicBlock) {
            return null; // BasicBlock应该已经在之前的步骤中被克隆
        }
        // 为指令创建克隆
        Instruction originalInstr = (Instruction) originalValue;
        cloneInstruction(originalInstr, originalInstr.getBasicBlock(), true);
        clonedInstructions.add(originalInstr);
        return valueMap.get(originalInstr);
    }

    /**
     * 克隆一条指令
     *
     * @param originalInstr 原始指令
     * @param parentBlock   父基本块
     * @param isLazyCloning 是否为延迟克隆
     */
    public static void cloneInstruction(Instruction originalInstr,
                                        BasicBlock parentBlock, boolean isLazyCloning) {
        if (clonedInstructions.contains(originalInstr)) {
            BasicBlock clonedParent = (BasicBlock) getOrCreate(parentBlock);
            clonedParent.addInstruction(
                    (Instruction) getOrCreate(originalInstr));
            return;
        }
        Instruction clonedInstr = createClonedInstruction(originalInstr, parentBlock);
        BasicBlock clonedParent = (BasicBlock) getOrCreate(parentBlock);
        clonedInstr.setBasicBlock(clonedParent);
        if (!isLazyCloning) {
            clonedParent.addInstruction(clonedInstr);
        }
        valueMap.put(originalInstr, clonedInstr);
    }

    // 修改后的 createClonedInstruction 方法
    private static Instruction createClonedInstruction(Instruction originalInstr, BasicBlock parentBlock) {
        BiFunction<Instruction, BasicBlock, Instruction> cloneFunc = cloneMap.get(originalInstr.getClass());
        if (cloneFunc != null) {
            return cloneFunc.apply(originalInstr, parentBlock);
        }
        throw new RuntimeException("Unsupported instruction type: " + originalInstr.getClass().getName());
    }

    // 克隆分支指令的辅助方法
    private static Instruction cloneBranchInst(BrInst brInst, BasicBlock parentBlock) {
        BasicBlock clonedBlock = (BasicBlock) getOrCreate(parentBlock);
        if (brInst.isConditional()) {
            BrInst clonedBrInst = new BrInst(
                    getOrCreate(brInst.getCondition()),
                    (BasicBlock) getOrCreate(brInst.getTrueBlock()),
                    (BasicBlock) getOrCreate(brInst.getFalseBlock()));
            updateConditionBranch(clonedBrInst, clonedBlock);
            return clonedBrInst;
        } else {
            BrInst clonedBrInst = new BrInst((BasicBlock) getOrCreate(brInst.getTrueBlock()));
            updateNoConditionBranch(clonedBrInst, clonedBlock);
            return clonedBrInst;
        }
    }

    // 克隆Phi指令的辅助方法
    private static Instruction clonePhiInst(PhiInst phiInst, BasicBlock parentBlock) {
        ArrayList<BasicBlock> copiedBlocks = new ArrayList<>();
        for (BasicBlock block : phiInst.getBlocks()) {
            copiedBlocks.add((BasicBlock) getOrCreate(block));
        }
        PhiInst clonedPhi = new PhiInst(phiInst.getValueType(), (BasicBlock) getOrCreate(parentBlock), copiedBlocks);
        phiInstructions.add(phiInst);
        return clonedPhi;
    }

    private static void updateConditionBranch(BrInst clonedBrInst, BasicBlock clonedParent) {
        BasicBlock trueBlock = (BasicBlock) clonedBrInst.getOperands().get(1);
        BasicBlock falseBlock = (BasicBlock) clonedBrInst.getOperands().get(2);
        clonedParent.addNextBlock(trueBlock);
        clonedParent.addNextBlock(falseBlock);
        trueBlock.addPrevBlock(clonedParent);
        falseBlock.addPrevBlock(clonedParent);
    }

    private static void updateNoConditionBranch(BrInst clonedBrInst, BasicBlock clonedParent) {
        BasicBlock targetBlock = (BasicBlock) clonedBrInst.getOperands().get(0);
        clonedParent.addNextBlock(targetBlock);
        targetBlock.addPrevBlock(clonedParent);
    }
}
