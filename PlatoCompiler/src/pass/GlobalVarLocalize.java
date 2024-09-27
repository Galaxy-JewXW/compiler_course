package pass;

import frontend.TableManager;
import frontend.symbol.VarSymbol;
import middle.component.Module;
import middle.component.*;
import middle.component.instruction.AllocInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.StoreInst;
import middle.component.model.Use;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GlobalVarLocalize {
    private static HashMap<GlobalVar, HashSet<Function>> usedMap;
    private static HashMap<Function, HashSet<Function>> calledMap;

    public static void build(Module module) {
        usedMap = new HashMap<>();
        calledMap = new HashMap<>();
        checkUse(module);
        createCallMap(module);
        localize(module);
    }

    private static void checkUse(Module module) {
        for (GlobalVar gv : module.getGlobalVars()) {
            for (Use use : gv.getUseList()) {
                Function func = ((Instruction) use.getUser())
                        .getBasicBlock().getFunction();
                usedMap.computeIfAbsent(gv, k -> new HashSet<>()).add(func);
            }
        }
    }

    private static void createCallMap(Module module) {
        for (Function func : module.getFunctions()) {
            for (BasicBlock block : func.getBasicBlocks()) {
                for (Instruction instr : block.getInstructions()) {
                    if (instr instanceof CallInst call) {
                        Function target = call.getCalledFunction();
                        calledMap.computeIfAbsent(target, k -> new HashSet<>()).add(func);
                    }
                }
            }
        }
    }

    private static void localize(Module module) {
        ArrayList<GlobalVar> toRemove = new ArrayList<>();
        for (GlobalVar gv : module.getGlobalVars()) {
            HashSet<Function> users = usedMap.getOrDefault(gv, null);
            if (users == null) {
                toRemove.add(gv);
                continue;
            }
            if (users.size() >= 2) {
                continue;
            }
            Function func = users.iterator().next();
            if (canLocalize(func, gv)) {
                BasicBlock entryBlock = func.getEntryBlock();
                ValueType gvType = ((PointerType) gv.getValueType()).getTargetType();
                AllocInst allocInst = new AllocInst(gvType);
                allocInst.setBasicBlock(entryBlock);
                entryBlock.getInstructions().add(0, allocInst);
                int initValue;
                VarSymbol varSymbol = (VarSymbol) TableManager.getInstance()
                        .getSymbol(gv.getName().substring(1));
                InitialValue initialValue = varSymbol.getInitialValue();
                if (initialValue.getElements() == null) {
                    initValue = 0;
                } else {
                    initValue = varSymbol.getConstValue();
                }
                StoreInst storeInst = new StoreInst(allocInst, new ConstInt(gvType, initValue));
                storeInst.setBasicBlock(entryBlock);
                entryBlock.getInstructions().add(1, storeInst);
                gv.replaceByNewValue(allocInst);
                toRemove.add(gv);
            }
        }
        module.getGlobalVars().removeAll(toRemove);
    }

    private static boolean canLocalize(Function func, GlobalVar globalVar) {
        if (calledMap.containsKey(func)) {
            return false;
        }
        if (globalVar.isConstant()) {
            // 全局常量的处理在ConstToValue中
            return false;
        }
        ValueType gvType = ((PointerType) globalVar.getValueType()).getTargetType();
        return gvType.equals(IntegerType.i8) || gvType.equals(IntegerType.i32);
    }

}
