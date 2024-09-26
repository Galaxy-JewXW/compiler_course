package optimize;

import middle.IRData;
import middle.component.*;
import middle.component.instruction.*;
import middle.component.instruction.io.GetintInst;
import middle.component.instruction.io.PutintInst;
import middle.component.instruction.io.PutstrInst;
import middle.component.model.Value;

import java.util.HashMap;
import java.util.HashSet;

public class FunctionCopy {
    private static final HashMap<Value, Value> valueMap = new HashMap<>();
    private static final HashSet<BasicBlock> visitedBlocks = new HashSet<>();
    private static final HashSet<PhiInst> phiInsts = new HashSet<>();
    private static final HashSet<Instruction> clonedInstructions = new HashSet<>();
    private static Function caller;

    /**
     * 克隆一个函数，创建其副本以用于内联优化
     *
     * @param originalFunc 要克隆的原始函数
     * @param callerFunc   调用者函数
     * @return 克隆后的新函数
     */
    public static Function copyFunction(Function originalFunc, Function callerFunc) {
        caller = callerFunc;
        Function clonedFunc = createClonedFunctionSkeleton(originalFunc);
        for (BasicBlock block : originalFunc.getBasicBlocks()) {
            BasicBlock clonedBlock = new BasicBlock(IRData.getBlockName(), clonedFunc);
            clonedFunc.addBasicBlock(clonedBlock);
            valueMap.put(block, clonedBlock);
        }
        cloneBlockContents(originalFunc.getBasicBlocks().get(0));
        for (PhiInst phi : phiInsts) {
            PhiInst clonedPhi = (PhiInst) getOrCreateClonedValue(phi);
            for (int i = 0; i < phi.getOperands().size(); i++) {
                clonedPhi.addValue(
                        (BasicBlock) getOrCreateClonedValue(phi.getBlocks().get(i)),
                        getOrCreateClonedValue(phi.getOperands().get(i))
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
    public static Value getOrCreateClonedValue(Value originalValue) {
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
            BasicBlock clonedParent = (BasicBlock) getOrCreateClonedValue(parentBlock);
            clonedParent.addInstruction(
                    (Instruction) getOrCreateClonedValue(originalInstr));
            return;
        }
        Instruction clonedInstr = createClonedInstruction(originalInstr, parentBlock);
        if (!isLazyCloning) {
            BasicBlock clonedParent = (BasicBlock) getOrCreateClonedValue(parentBlock);
            clonedParent.addInstruction(clonedInstr);
        }
        valueMap.put(originalInstr, clonedInstr);
    }

    private static Instruction createClonedInstruction(Instruction originalInstr, BasicBlock parentBlock) {
        BasicBlock clonedBlock = (BasicBlock) getOrCreateClonedValue(parentBlock);
        if (originalInstr instanceof GetintInst) {
            return new GetintInst(clonedBlock);
        } else if (originalInstr instanceof PutintInst) {
            return new PutintInst(getOrCreateClonedValue(originalInstr.getOperands().get(0)), clonedBlock);
        } else if (originalInstr instanceof PutstrInst putstrInst) {
            return new PutstrInst(putstrInst.getConstString(), clonedBlock);
        } else if (originalInstr instanceof AllocInst allocInst) {
            return new AllocInst(allocInst.getTargetType(), clonedBlock);
        } else if (originalInstr instanceof BinaryInst binaryInst) {
            return new BinaryInst(binaryInst.getOpType(),
                    getOrCreateClonedValue(originalInstr.getOperands().get(0)),
                    getOrCreateClonedValue(originalInstr.getOperands().get(1)),
                    clonedBlock);
        } else if (originalInstr instanceof BrInst brInst) {
            BrInst clonedBrInst;
            if (brInst.isConditional()) {
                clonedBrInst = new BrInst(clonedBlock, brInst.getCondition(),
                        brInst.getTrueBlock(), brInst.getFalseBlock());
                updateBranchRelationships(clonedBrInst, clonedBlock);
            } else {
                clonedBrInst = new BrInst(clonedBlock, brInst.getTrueBlock());
                updateJumpRelationships(clonedBrInst, clonedBlock);
            }
            return clonedBrInst;
        } else if (originalInstr instanceof CallInst) {
            throw new UnsupportedOperationException("Call instructions are not supported in function cloning");
        } else if (originalInstr instanceof GepInst) {
            return new GepInst(getOrCreateClonedValue(originalInstr.getOperands().get(0)),
                    getOrCreateClonedValue(originalInstr.getOperands().get(1)),
                    clonedBlock);
        } else if (originalInstr instanceof LoadInst) {
            return new LoadInst(getOrCreateClonedValue(originalInstr.getOperands().get(0)), clonedBlock);
        } else if (originalInstr instanceof PhiInst phiInst) {
            PhiInst clonedPhi = new PhiInst(phiInst.getValueType(), clonedBlock);
            phiInsts.add(phiInst);
            return clonedPhi;
        } else if (originalInstr instanceof RetInst) {
            return new RetInst(getOrCreateClonedValue(originalInstr.getOperands().get(0)), clonedBlock);
        } else if (originalInstr instanceof StoreInst) {
            return new StoreInst(getOrCreateClonedValue(originalInstr.getOperands().get(0)),
                    getOrCreateClonedValue(originalInstr.getOperands().get(1)),
                    clonedBlock);
        } else if (originalInstr instanceof ZextInst zextInst) {
            return new ZextInst(
                    getOrCreateClonedValue(originalInstr.getOperands().get(0)),
                    zextInst.getValueType(), clonedBlock);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private static void updateBranchRelationships(BrInst clonedBrInst, BasicBlock clonedParent) {
        BasicBlock trueBlock = (BasicBlock) getOrCreateClonedValue(clonedBrInst.getOperands().get(1));
        BasicBlock falseBlock = (BasicBlock) getOrCreateClonedValue(clonedBrInst.getOperands().get(2));

        clonedParent.addNextBlock(trueBlock);
        clonedParent.addNextBlock(falseBlock);
        trueBlock.addPrevBlock(clonedParent);
        falseBlock.addPrevBlock(clonedParent);
    }

    private static void updateJumpRelationships(BrInst clonedJmp, BasicBlock clonedParent) {
        BasicBlock targetBlock = (BasicBlock) getOrCreateClonedValue(clonedJmp.getOperands().get(0));
        clonedParent.addNextBlock(targetBlock);
        targetBlock.addPrevBlock(clonedParent);
    }
}