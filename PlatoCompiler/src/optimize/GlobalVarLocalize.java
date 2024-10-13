package optimize;

import frontend.TableManager;
import frontend.symbol.VarSymbol;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.InitialValue;
import middle.component.Module;
import middle.component.instruction.AllocInst;
import middle.component.instruction.CallInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.LoadInst;
import middle.component.instruction.StoreInst;
import middle.component.model.User;
import middle.component.type.ArrayType;
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
        Mem2Reg.run(module, false);
        usedMap = new HashMap<>();
        calledMap = new HashMap<>();
        checkUse(module);
        createCallMap(module);
        localize(module);
        constGlobalVarToValue(module);
        constGlobalArrayToValue(module);
        MemoryOptimize.run(module);
        Mem2Reg.run(module, true);
        CodeRemoval.run(module);
    }

    private static void checkUse(Module module) {
        for (GlobalVar gv : module.getGlobalVars()) {
            for (User user : gv.getUserList()) {
                Function func = ((Instruction) user)
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
        ValueType gvType = ((PointerType) globalVar.getValueType()).getTargetType();
        return gvType.equals(IntegerType.i8) || gvType.equals(IntegerType.i32);
    }

    private static void constGlobalVarToValue(Module module) {
        ArrayList<GlobalVar> toRemove = new ArrayList<>();
        for (GlobalVar gv : module.getGlobalVars()) {
            HashSet<Function> users = usedMap.getOrDefault(gv, null);
            VarSymbol varSymbol = (VarSymbol) TableManager.getInstance()
                    .getSymbol(gv.getName().substring(1));
            if (!varSymbol.isConstant()) {
                continue;
            }
            if (users == null) {
                toRemove.add(gv);
                continue;
            }
            ValueType gvType = ((PointerType) gv.getValueType()).getTargetType();
            if (gvType.equals(IntegerType.i8) || gvType.equals(IntegerType.i32)) {
                int initValue;
                InitialValue initialValue = varSymbol.getInitialValue();
                if (initialValue.getElements() == null) {
                    initValue = 0;
                } else {
                    initValue = varSymbol.getConstValue();
                }
                ConstInt constInt = new ConstInt(gvType, initValue);
                for (User user : gv.getUserList()) {
                    if (user instanceof LoadInst loadInst) {
                        loadInst.replaceByNewValue(constInt);
                        toRemove.add(gv);
                        loadInst.getBasicBlock().getInstructions().remove(loadInst);
                    }
                }
            }
        }
        module.getGlobalVars().removeAll(toRemove);
    }

    private static void constGlobalArrayToValue(Module module) {
        ArrayList<GlobalVar> toRemove = new ArrayList<>();
        for (GlobalVar gv : module.getGlobalVars()) {
            HashSet<Function> users = usedMap.getOrDefault(gv, null);
            if (users == null) {
                toRemove.add(gv);
                continue;
            }
            VarSymbol varSymbol = (VarSymbol) TableManager.getInstance()
                    .getSymbol(gv.getName().substring(1));
            if (!varSymbol.isConstant()) {
                continue;
            }
            ValueType gvType = ((PointerType) gv.getValueType()).getTargetType();
            if (!(gvType instanceof ArrayType arrayType)) {
                continue;
            }
            gvType = arrayType.getElementType();
            if (gvType.equals(IntegerType.i8) || gvType.equals(IntegerType.i32)) {
                for (User user : gv.getUserList()) {
                    if (user instanceof GepInst gepInst) {
                        if (gepInst.getIndex() instanceof ConstInt constInt) {
                            int index = constInt.getIntValue();
                            for (User user1 : gepInst.getUserList()) {
                                if (user1 instanceof LoadInst loadInst) {
                                    int intValue;
                                    if (index < varSymbol.getInitialValue().getElements().size()) {
                                        intValue = varSymbol.getConstValue(index);
                                    } else {
                                        intValue = 0;
                                    }
                                    ConstInt constInt1 = new ConstInt(gvType, intValue);
                                    loadInst.replaceByNewValue(constInt1);
                                    toRemove.add(gv);
                                    loadInst.getBasicBlock().getInstructions().remove(loadInst);
                                }
                            }
                        }
                    }
                }
            }
        }
        module.getGlobalVars().removeAll(toRemove);
    }

}
