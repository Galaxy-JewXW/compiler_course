package optimize;

import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.LoadInst;
import middle.component.instruction.StoreInst;
import middle.component.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryOptimize {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                optimizeGlobalVar(basicBlock);
                uselessLoad(basicBlock);
            }
        }
    }

    private static void optimizeGlobalVar(BasicBlock basicBlock) {
        HashMap<GlobalVar, Value> gvMap = new HashMap<>();
        HashMap<GlobalVar, Value> writeMap = new HashMap<>();
        ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
        for (Instruction instruction : instructions) {
            if (instruction instanceof StoreInst storeInst) {
                if (!(storeInst.getPointer() instanceof GlobalVar globalVar)) {
                    continue;
                }
                gvMap.put(globalVar, storeInst.getStoredValue());
                writeMap.put(globalVar, storeInst.getStoredValue());
                storeInst.removeOperands();
                basicBlock.getInstructions().remove(instruction);
            } else if (instruction instanceof LoadInst loadInst) {
                if (!(loadInst.getPointer() instanceof GlobalVar globalVar)) {
                    continue;
                }
                if (gvMap.containsKey(globalVar)) {
                    loadInst.replaceByNewValue(gvMap.get(globalVar));
                    loadInst.removeOperands();
                    basicBlock.getInstructions().remove(instruction);
                } else {
                    gvMap.put(globalVar, loadInst);
                }
            } else if (instruction instanceof CallInst callInst) {
                gvMap.clear();
                for (Map.Entry<GlobalVar, Value> entry : writeMap.entrySet()) {
                    StoreInst storeInst = new StoreInst(entry.getKey(), entry.getValue());
                    storeInst.setBasicBlock(basicBlock);
                    basicBlock.getInstructions().add(basicBlock.getInstructions().indexOf(callInst), storeInst);
                }
                writeMap.clear();
            }
        }
        for (Map.Entry<GlobalVar, Value> entry : writeMap.entrySet()) {
            StoreInst storeInst = new StoreInst(entry.getKey(), entry.getValue());
            storeInst.setBasicBlock(basicBlock);
            basicBlock.getInstructions().add(basicBlock.getInstructions().size() - 1, storeInst);
        }
    }

    private static void uselessLoad(BasicBlock basicBlock) {
        ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
        HashMap<Value, Value> addressMap = new HashMap<>();
        for (Instruction instruction : instructions) {
            if (instruction instanceof StoreInst storeInst) {
                addressMap.clear();
                addressMap.put(storeInst.getPointer(), storeInst.getStoredValue());
            } else if (instruction instanceof LoadInst loadInst) {
                Value address = loadInst.getPointer();
                if (addressMap.containsKey(address)) {
                    loadInst.replaceByNewValue(addressMap.get(address));
                    loadInst.removeOperands();
                    basicBlock.getInstructions().remove(instruction);
                } else {
                    addressMap.put(address, loadInst);
                }
            } else if (instruction instanceof CallInst) {
                addressMap.clear();
            }
        }
    }
}
