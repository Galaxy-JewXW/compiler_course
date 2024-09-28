package pass;

import middle.IRData;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.FuncParam;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.instruction.AllocInst;
import middle.component.instruction.BinaryInst;
import middle.component.instruction.BrInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.LoadInst;
import middle.component.instruction.PhiInst;
import middle.component.instruction.RetInst;
import middle.component.instruction.StoreInst;
import middle.component.instruction.TruncInst;
import middle.component.instruction.ZextInst;
import middle.component.instruction.io.GetcharInst;
import middle.component.instruction.io.GetintInst;
import middle.component.instruction.io.PutchInst;
import middle.component.instruction.io.PutintInst;
import middle.component.instruction.io.PutstrInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FunctionCopy {
    private static final HashMap<Value, Value> valueMap = new HashMap<>();
    private static final HashSet<BasicBlock> visitedBlocks = new HashSet<>();
    private static final HashSet<PhiInst> phiInstructions = new HashSet<>();
    private static final HashSet<Instruction> clonedInstructions = new HashSet<>();

    /**
     * 克隆一个函数，创建其副本以用于内联优化
     *
     * @param originalFunc 要克隆的原始函数
     * @return 克隆后的新函数
     */
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

    private static Instruction createClonedInstruction(Instruction originalInstr, BasicBlock parentBlock) {
        BasicBlock clonedBlock = (BasicBlock) getOrCreate(parentBlock);
        if (originalInstr instanceof GetintInst) {
            return new GetintInst();
        } else if (originalInstr instanceof GetcharInst) {
            return new GetcharInst();
        } else if (originalInstr instanceof PutintInst putintInst) {
            return new PutintInst(getOrCreate(putintInst.getTarget()));
        } else if (originalInstr instanceof PutchInst putchInst) {
            return new PutchInst(getOrCreate(putchInst.getTarget()));
        } else if (originalInstr instanceof PutstrInst putstrInst) {
            return new PutstrInst(putstrInst.getConstString());
        } else if (originalInstr instanceof AllocInst allocInst) {
            return new AllocInst(allocInst.getTargetType());
        } else if (originalInstr instanceof BinaryInst binaryInst) {
            return new BinaryInst(binaryInst.getOpType(),
                    getOrCreate(binaryInst.getOperand1()),
                    getOrCreate(binaryInst.getOperand2()));
        } else if (originalInstr instanceof BrInst brInst) {
            BrInst clonedBrInst;
            if (brInst.isConditional()) {
                clonedBrInst = new BrInst(getOrCreate(brInst.getCondition()),
                        (BasicBlock) getOrCreate(brInst.getTrueBlock()),
                        (BasicBlock) getOrCreate(brInst.getFalseBlock()));
                updateConditionBranch(clonedBrInst, clonedBlock);
            } else {
                clonedBrInst = new BrInst((BasicBlock) getOrCreate(brInst.getTrueBlock()));
                updateNoConditionBranch(clonedBrInst, clonedBlock);
            }
            return clonedBrInst;
        } else if (originalInstr instanceof CallInst) {
            throw new RuntimeException("Call instructions are not supported in function cloning");
        } else if (originalInstr instanceof GepInst gepInst) {
            return new GepInst(getOrCreate(gepInst.getPointer()),
                    getOrCreate(gepInst.getIndex()));
        } else if (originalInstr instanceof LoadInst loadInst) {
            return new LoadInst(getOrCreate(loadInst.getPointer()));
        } else if (originalInstr instanceof PhiInst phiInst) {
            ArrayList<BasicBlock> copiedBlocks = new ArrayList<>();
            for (BasicBlock block : phiInst.getBlocks()) {
                copiedBlocks.add((BasicBlock) getOrCreate(block));
            }
            PhiInst clonedPhi = new PhiInst(phiInst.getValueType(), clonedBlock, copiedBlocks);
            phiInstructions.add(phiInst);
            return clonedPhi;
        } else if (originalInstr instanceof RetInst retInst) {
            return new RetInst(getOrCreate(retInst.getReturnValue()));
        } else if (originalInstr instanceof StoreInst storeInst) {
            return new StoreInst(getOrCreate(storeInst.getPointer()),
                    getOrCreate(storeInst.getStoredValue()));
        } else if (originalInstr instanceof ZextInst zextInst) {
            return new ZextInst(
                    getOrCreate(zextInst.getOriginValue()),
                    zextInst.getValueType());
        } else if (originalInstr instanceof TruncInst truncInst) {
            return new TruncInst(getOrCreate(truncInst.getOriginValue()),
                    truncInst.getValueType());
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
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
