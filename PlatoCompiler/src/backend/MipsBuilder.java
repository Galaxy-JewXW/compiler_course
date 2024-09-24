package backend;

import backend.enums.AsmOp;
import backend.enums.Register;
import backend.global.Asciiz;
import backend.global.Word;
import backend.text.Comment;
import backend.text.JumpAsm;
import backend.text.Label;
import middle.component.ConstString;
import middle.component.Function;
import middle.component.GlobalVar;
import middle.component.Module;
import middle.component.model.Value;
import middle.component.type.IntegerType;
import middle.component.type.PointerType;
import middle.component.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsBuilder {
    private final Module module;
    private int curStackOffset;
    private HashMap<Value, Integer> stackOffsetMap;
    private HashMap<Value, Register> var2reg;

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
        new Comment("\n# enter main function");
        new JumpAsm(AsmOp.JAL, "main");
        new JumpAsm(AsmOp.J, "end");
        for (Function function : module.getFunctions()) {
            buildFunction(function);
        }
        new Label("end");
    }

    private void buildConstString(ConstString constString) {
        new Asciiz("s" + constString.getName().substring(4),
                constString.getContent());
    }

    private void buildGlobalVar(GlobalVar globalVar) {
        ValueType targetType = ((PointerType) globalVar.getValueType())
                .getTargetType();
        if (targetType.equals(IntegerType.i32) || targetType.equals(IntegerType.i8)) {
            if (globalVar.getInitialValue().getElements() == null) {
                new Word(globalVar.getName().substring(1), 0);
            } else {
                new Word(globalVar.getName().substring(1),
                        globalVar.getInitialValue().getElements().get(0));
            }
        } else {
            // 初始化全局数组
            ArrayList<Integer> list = globalVar.getInitialValue().getElements();
            int length = globalVar.getInitialValue().getLength();
            new Word(globalVar.getName().substring(1), list, length);
        }
    }

    private void buildFunction(Function function) {
        String name = function.getName().substring(1);
    }

    private void enterFunc() {
        curStackOffset = 0;
        stackOffsetMap = new HashMap<>();
    }
}
