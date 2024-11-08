package optimize;

import middle.component.BasicBlock;
import middle.component.FuncParam;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.StoreInst;
import middle.component.instruction.io.IOInst;
import middle.component.model.Value;

import java.util.HashMap;
import java.util.HashSet;

public class FunctionSideEffect {
    public static void run(Module module) {
        HashMap<Function, HashSet<Function>> callMap = new HashMap<>();
        for (Function function : module.getFunctions()) {
            boolean hasSideEffects = false;
            HashSet<Function> calledFunctions = new HashSet<>();
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (instruction instanceof CallInst callInst) {
                        calledFunctions.add(callInst.getCalledFunction());
                    } else if (instruction instanceof IOInst) {
                        hasSideEffects = true;
                        break;
                    } else if (instruction instanceof StoreInst storeInst) {
                        Value target = storeInst.getPointer();
                        if (target instanceof GlobalVar) {
                            hasSideEffects = true;
                            break;
                        } else if (target instanceof GepInst gepInst) {
                            if (gepInst.getPointer() instanceof FuncParam
                                    || gepInst.getPointer() instanceof GlobalVar) {
                                hasSideEffects = true;
                                break;
                            }
                        }
                    }
                }
                if (hasSideEffects) {
                    break;
                }
            }
            callMap.put(function, calledFunctions);
            function.setHasSideEffects(hasSideEffects);
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Function function : callMap.keySet()) {
                HashSet<Function> calledFunctions = callMap.get(function);
                boolean hasSideEffects = false;
                for (Function calledFunction : calledFunctions) {
                    hasSideEffects |= calledFunction.hasSideEffects();
                }
                if (hasSideEffects && !function.hasSideEffects()) {
                    function.setHasSideEffects(true);
                    changed = true;
                    break;
                }
            }
        }
    }
}
