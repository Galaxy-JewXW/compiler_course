package pass;

import frontend.TableManager;
import frontend.symbol.VarSymbol;
import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.InitialValue;
import middle.component.Module;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.LoadInst;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class ConstToValue {
    public static void run(Module module) {
        ArrayList<GlobalVar> toRemove = new ArrayList<>();
        // 处理一维常量
        for (GlobalVar gv : module.getGlobalVars()) {
            if (!gv.isConstant()) {
                continue;
            }
            ValueType valueType = ((PointerType) gv.getValueType()).getTargetType();
            if (valueType.equals(IntegerType.i8)
                    || valueType.equals(IntegerType.i32)) {
                VarSymbol varSymbol = (VarSymbol) TableManager.getInstance()
                        .getSymbol(gv.getName().substring(1));
                ConstInt constInt = new ConstInt(valueType, varSymbol.getConstValue());
                gv.replaceByNewValue(constInt);
                toRemove.add(gv);
            }
        }
        module.getGlobalVars().removeAll(toRemove);

        // 处理数组常量
        for (Function func : module.getFunctions()) {
            for (BasicBlock block : func.getBasicBlocks()) {
                ArrayList<Instruction> list = new ArrayList<>(block.getInstructions());
                ArrayList<Instruction> buffer = new ArrayList<>();
                for (Instruction inst : list) {
                    if (inst instanceof LoadInst loadInst
                            && loadInst.getPointer() instanceof GepInst gepInst
                            && gepInst.getPointer() instanceof GlobalVar globalVar
                            && globalVar.isConstant()
                            && gepInst.getIndex() instanceof ConstInt constInt) {
                        int index = constInt.getIntValue();
                        InitialValue initialValue = globalVar.getInitialValue();
                        int valueInt;
                        if (index < initialValue.getElements().size()) {
                            valueInt = initialValue.getElements().get(index);
                        } else {
                            valueInt = 0;
                        }
                        ValueType valueType = ((ArrayType) initialValue.getValueType())
                                .getElementType();
                        Value value = new ConstInt(valueType, valueInt);
                        loadInst.replaceByNewValue(value);
                        buffer.add(loadInst);
                    }
                }
                block.getInstructions().removeAll(buffer);
            }
        }
        module.getGlobalVars().removeAll(toRemove);

        for (Function func : module.getFunctions()) {
            for (BasicBlock block : func.getBasicBlocks()) {
                ArrayList<Instruction> list = new ArrayList<>(block.getInstructions());
                for (Instruction inst : list) {
                    if (inst instanceof LoadInst loadInst
                            && loadInst.getPointer() instanceof ConstInt constInt) {
                        loadInst.replaceByNewValue(constInt);
                        loadInst.deleteUse();
                        block.getInstructions().remove(inst);
                    }
                }
            }
        }
    }
}
