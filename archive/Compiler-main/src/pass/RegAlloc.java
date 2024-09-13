package pass;

import IR.IRModule;
import IR.values.*;
import IR.values.instructions.ConvInst;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.PhiInst;
import backend.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RegAlloc {
    private final IRModule irModule;
    /* activeness  analysis*/
    private HashMap<BasicBlock, HashSet<Value>> inMap;
    private HashMap<BasicBlock, HashSet<Value>> outMap;
    private HashMap<BasicBlock, HashSet<Value>> defMap;//先赋值后用
    private HashMap<BasicBlock, HashSet<Value>> useMap;//先用后赋值
    /* register alloc */
    private final ArrayList<Register> registerPool;
    private final int K;
    private HashMap<Register, Value> reg2var;
    private HashMap<Value, Register> var2reg;

    public RegAlloc(IRModule irModule) {
        this.irModule = irModule;
        this.registerPool = new ArrayList<>();
        for (Register reg : Register.values()) {
            if (reg.ordinal() >= Register.T0.ordinal() && reg.ordinal() <= Register.T9.ordinal()) {
                registerPool.add(reg);
            }
        }
        this.K = registerPool.size();
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            initActivenessAnalyze(function);
            calcInOut(function);
            initAllocReg();
            allocRegister(function.getBasicBlocks().get(0));
            function.setVar2reg(var2reg);
//            System.out.println(function.getName());
//            for (Value value : var2reg.keySet()) {
//                System.out.println(value.getName() + " <- " + var2reg.get(value));
//            }
        }
    }

    private void initActivenessAnalyze(Function function) {
        inMap = new HashMap<>();
        outMap = new HashMap<>();
        defMap = new HashMap<>();
        useMap = new HashMap<>();
        for (BasicBlock bb : function.getBasicBlocks()) {
            inMap.put(bb, new HashSet<>());
            outMap.put(bb, new HashSet<>());
            defMap.put(bb, new HashSet<>());
            useMap.put(bb, new HashSet<>());
            calcDefUse(bb);
        }
    }

    private void calcDefUse(BasicBlock basicBlock) {
        HashSet<Value> defList = defMap.get(basicBlock);
        HashSet<Value> useList = useMap.get(basicBlock);
        for (Instruction instr : basicBlock.getInstructions()) {
            if (instr instanceof PhiInst) {
                for (Value value : instr.getOperands()) {
                    if (conflictAble(value)) {
                        useList.add(value);
                    }
                }
            }
        }
        for (Instruction instr : basicBlock.getInstructions()) {
            for (Value value : instr.getOperands()) {
                if (!defList.contains(value) && conflictAble(value)) {
                    useList.add(value);
                }
            }
            if (!useList.contains(instr) && !instr.getName().isEmpty()) {
                defList.add(instr);
            }
        }
    }

    private boolean conflictAble(Value value) {
        //return value instanceof Instruction || value instanceof Argument || value instanceof GlobalVar;
        return !(value instanceof Const);
    }

    private void calcInOut(Function function) {
        ArrayList<BasicBlock> bbList = function.getBasicBlocks();
        boolean update = true;
        while (update) {
            update = false;
            for (int i = bbList.size() - 1; i >= 0; i--) {
                BasicBlock basicBlock = bbList.get(i);
                HashSet<Value> out = new HashSet<>();
                for (BasicBlock bb : basicBlock.getNext()) {
                    out.addAll(inMap.get(bb));
                }
                outMap.put(basicBlock, out);
                HashSet<Value> in = new HashSet<>(out);
                in.removeAll(defMap.get(basicBlock));
                in.addAll(useMap.get(basicBlock));
                if (!in.equals(inMap.get(basicBlock))) {
                    update = true;
                    inMap.put(basicBlock, in);
                }
            }
        }
    }

    private void initAllocReg() {
        this.reg2var = new HashMap<>();
        this.var2reg = new HashMap<>();
    }

    private void allocRegister(BasicBlock entry) {
        HashMap<Value, Value> lastUse = new HashMap<>();
        HashSet<Value> unusedAfter = new HashSet<>();
        HashSet<Value> defined = new HashSet<>();
        for (Instruction user : entry.getInstructions()) {
            for (Value used : user.getOperands()) {
                lastUse.put(used, user);
            }
        }
        for (Instruction instr : entry.getInstructions()) {
            if (!(instr instanceof PhiInst)) {
                for (Value value : instr.getOperands()) {
                    if (lastUse.get(value) == instr && !outMap.get(entry).contains(value)) {
                        if (var2reg.containsKey(value)) {
                            reg2var.remove(var2reg.get(value));
                            unusedAfter.add(value);
                        }
                    }
                }
            }
            if (!instr.getName().isEmpty() && !(instr instanceof ConvInst)) {
                defined.add(instr);
                Register reg = getReg();
                if (reg2var.containsKey(reg)) {
                    var2reg.remove(reg2var.get(reg));
                }
                reg2var.put(reg, instr);
                var2reg.put(instr, reg);
            }
        }
        for (BasicBlock bb : entry.getImmDomList()) {
            HashMap<Register, Value> buf = new HashMap<>();
            for (Register reg : reg2var.keySet()) {
                if (!inMap.get(bb).contains(reg2var.get(reg))) {
                    buf.put(reg, reg2var.get(reg));
                }
            }
            for (Register reg : buf.keySet()) {
                reg2var.remove(reg);
            }
            allocRegister(bb);
            for (Register reg : buf.keySet()) {
                reg2var.put(reg, buf.get(reg));
            }
        }
        for (Value value : defined) {
            if (var2reg.containsKey(value)) {
                reg2var.remove(var2reg.get(value));
            }
        }
        for (Value value : unusedAfter) {
            if (var2reg.containsKey(value) && !defined.contains(value)) {
                reg2var.put(var2reg.get(value), value);
            }
        }
    }

    int cnt = 0;

    private Register getReg() {
        for (Register reg : registerPool) {
            if (!reg2var.containsKey(reg)) {
                return reg;
            }
        }
        if (cnt >= K) {
            cnt = 0;
        }
        return registerPool.get(cnt++);
    }
}
