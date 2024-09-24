package backend;

import backend.global.Word;
import middle.component.ConstString;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class MipsBuilder {
    private final Module module;

    public MipsBuilder(Module module) {
        this.module = module;
    }

    public void build() {
        for (ConstString constString : module.getConstStrings()) {
            buildConstString(constString);
        }
        for (GlobalVar globalVar : module.getGlobalVars()) {
            buildGlobalVar(globalVar);
        }
    }

    private void buildConstString(ConstString constString) {

    }

    private void buildGlobalVar(GlobalVar globalVar) {
        ValueType targetType = ((PointerType) globalVar.getValueType())
                .getTargetType();
        Word word;
        if (targetType.equals(IntegerType.i32) || targetType.equals(IntegerType.i8)) {
            if (globalVar.getInitialValue().getElements() == null) {
                word = new Word(globalVar.getName().substring(1), 0);
            } else {
                word = new Word(globalVar.getName().substring(1),
                        globalVar.getInitialValue().getElements().get(0));
            }
        } else {
            // 初始化全局数组
            ArrayList<Integer> list = globalVar.getInitialValue().getElements();
            int length = globalVar.getInitialValue().getLength();
            word = new Word(globalVar.getName().substring(1), list, length);
        }
        System.out.println(word);
    }
}
