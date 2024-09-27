package pass;

import frontend.SymbolTable;
import frontend.TableManager;
import frontend.symbol.Symbol;
import frontend.symbol.VarSymbol;
import middle.component.Module;
import middle.component.*;
import middle.component.instruction.AllocInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.Instruction;
import middle.component.instruction.LoadInst;
import middle.component.model.Use;
import middle.component.model.User;
import middle.component.model.Value;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashSet;

public class ConstToValue {
    public static void run(Module module) {
        replaceGlobalConst(module);
        final SymbolTable root = TableManager.getInstance().getCurrentTable();
        for (SymbolTable symbolTable : root.getChildren()) {
            replaceLocalConst(symbolTable);
        }
    }

    private static void replaceGlobalConst(Module module) {
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
    }

    private static void replaceLocalConst(SymbolTable symbolTable) {
        HashSet<Symbol> varSymbols = new HashSet<>(symbolTable.getAllSymbols());
        varSymbols.removeIf(symbol -> !(symbol instanceof VarSymbol));
        for (Symbol symbol : varSymbols) {
            VarSymbol varSymbol = (VarSymbol) symbol;
            if (!varSymbol.isConstant()) {
                continue;
            }
            // value 应该是一个alloc指令
            Value value = varSymbol.getLlvmValue();
            if (!(value instanceof AllocInst)) {
                throw new RuntimeException("Shouldn't reach here");
            }
            ValueType valueType = ((PointerType) value.getValueType()).getTargetType();

            if (valueType instanceof IntegerType) {
                ConstInt constInt = new ConstInt(valueType, varSymbol.getConstValue());
                for (Use use : value.getUseList()) {
                    User user = use.getUser();
                    if (user instanceof LoadInst loadInst) {
                        loadInst.replaceByNewValue(constInt);
                    }
                }
            }

        }
        for (SymbolTable symbolTable1 : symbolTable.getChildren()) {
            replaceLocalConst(symbolTable1);
        }
    }
}
