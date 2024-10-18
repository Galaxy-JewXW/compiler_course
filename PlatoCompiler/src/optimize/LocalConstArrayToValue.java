package optimize;

import frontend.TableManager;
import frontend.symbol.VarSymbol;
import middle.component.ConstInt;
import middle.component.Module;
import middle.component.instruction.AllocInst;
import middle.component.instruction.GepInst;
import middle.component.instruction.LoadInst;
import middle.component.model.User;
import middle.component.type.ArrayType;
import middle.component.type.ValueType;

import java.util.HashSet;

public class LocalConstArrayToValue {
    public static void run(Module module) {
        HashSet<VarSymbol> localConstArray = TableManager.getInstance1().getLocalConstArray();
        for (VarSymbol var : localConstArray) {
            if (!(var.getLlvmValue() instanceof AllocInst allocInst)) {
                continue;
            }
            if (!(allocInst.getTargetType() instanceof ArrayType arrayType)) {
                continue;
            }
            ValueType type = arrayType.getElementType();
            for (User user : allocInst.getUserList()) {
                if (!(user instanceof GepInst gepInst)) {
                    continue;
                }
                if (!(gepInst.getIndex() instanceof ConstInt constInt)) {
                    continue;
                }
                int index = constInt.getIntValue();
                for (User user1 : gepInst.getUserList()) {
                    if (!(user1 instanceof LoadInst loadInst)) {
                        continue;
                    }
                    int intValue;
                    if (index < var.getInitialValue().getElements().size()) {
                        intValue = var.getConstValue(index);
                    } else {
                        intValue = 0;
                    }
                    ConstInt constInt1 = new ConstInt(type, intValue);
                    loadInst.replaceByNewValue(constInt1);
                    loadInst.getBasicBlock().getInstructions().remove(loadInst);
                }
            }
        }
        CodeRemoval.run(module);
    }
}
