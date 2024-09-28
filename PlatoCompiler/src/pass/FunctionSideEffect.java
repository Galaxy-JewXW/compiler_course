package pass;

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
            boolean sideEffect = false;
            HashSet<Function> callFunctions = new HashSet<>();
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
                    if (instruction instanceof CallInst callInst) {
                        callFunctions.add(callInst.getCalledFunction());
                    } else if (instruction instanceof IOInst) {
                        sideEffect = true;
                        break;
                    } else if (instruction instanceof StoreInst storeInst) {
                        Value pointer = storeInst.getPointer();
                        if (pointer instanceof GlobalVar) {
                            sideEffect = true;
                            break;
                        } else if (pointer instanceof GepInst gepInst) {
                            if (gepInst.getPointer() instanceof GlobalVar
                                    || gepInst.getPointer() instanceof FuncParam) {
                                sideEffect = true;
                                break;
                            }
                        }
                    }
                }
                if (sideEffect) {
                    break;
                }
            }
            function.setHasSideEffects(sideEffect);
            callMap.put(function, callFunctions);
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Function function : module.getFunctions()) {
                HashSet<Function> callFunctions = callMap.get(function);
                for (Function callFunction : callFunctions) {
                    if (callFunction.hasSideEffects()
                            && !function.hasSideEffects()) {
                        function.setHasSideEffects(true);
                        changed = true;
                        break;
                    }
                }
            }
        }
    }
}
