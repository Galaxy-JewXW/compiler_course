package pass;

import IR.IRModule;
import IR.values.*;
import IR.values.instructions.BinaryInst;
import IR.values.instructions.CallInst;
import IR.values.instructions.Instruction;
import IR.values.instructions.Operator;
import IR.values.instructions.men.GEPInst;
import IR.values.instructions.men.LoadInst;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class LVN {
    private final IRModule irModule;
    private HashMap<String, Instruction> map;

    public LVN(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            map = new HashMap<>();
            LVNVisit(function.getBasicBlocks().get(0));
        }
        for (Function function : irModule.getFunctions()) {
            for (BasicBlock bb : function.getBasicBlocks()) {
                optimizeCalc(bb);
            }
        }
    }

    private void LVNVisit(BasicBlock entry) {
        optimizeCalc(entry);
        HashSet<Instruction> inserted = new HashSet<>();
        Iterator<Instruction> it = entry.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instr = it.next();
            String hash = getGVNHash(instr);
            if (hash == null) {
                continue;
            }
            if (map.containsKey(hash)) {
                instr.replacedByNewVal(map.get(hash));
                instr.deleteUse();
                it.remove();
            } else {
                map.put(hash, instr);
                inserted.add(instr);
            }
        }
        for (BasicBlock bb : entry.getImmDomList()) {
            LVNVisit(bb);
        }
        for (Instruction instr : inserted) {
            map.remove(getGVNHash(instr));
        }
    }

    private void optimizeCalc(BasicBlock bb) {
        Iterator<Instruction> it = bb.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instr = it.next();
            if (instr instanceof LoadInst loadInst) {
                if (loadInst.pointer() instanceof GlobalVar globalVar && globalVar.isConst()) {
                    instr.replacedByNewVal(globalVar.getValue());
                    instr.deleteUse();
                    it.remove();
                }
            }
            if (!(instr instanceof BinaryInst binaryInst && binaryInst.isNumber())) {
                continue;
            }
            Value left = binaryInst.lVal();
            Value right = binaryInst.rVal();
            Operator op = binaryInst.getOp();
            int cnt = 0;
            if (left instanceof ConstInt) cnt++;
            if (right instanceof ConstInt) cnt++;
            if (cnt == 1) {
                Value ans = calcSingleCon(left, right, op);
                if (ans != null) {
                    instr.replacedByNewVal(ans);
                    instr.deleteUse();
                    it.remove();
                }
            } else if (cnt == 2) {
                Value ans = calcDoubleCon(left, right, op);
                instr.replacedByNewVal(ans);
                instr.deleteUse();
                it.remove();
            } else {
                Value ans = calcNoneCon(instr);
                if (ans != null) {
                    instr.replacedByNewVal(ans);
                    instr.deleteUse();
                    it.remove();
                }
            }
        }
    }

    private Value calcDoubleCon(Value left, Value right, Operator op) {
        int lVal = ((ConstInt) left).getVal();
        int rVal = ((ConstInt) right).getVal();
        int ans = 0;
        switch (op) {
            case Add -> ans = lVal + rVal;
            case Sub -> ans = lVal - rVal;
            case Mul -> ans = lVal * rVal;
            case SDiv -> ans = lVal / rVal;
            case Mod -> ans = lVal % rVal;
        }
        return new ConstInt(ans);
    }

    private Value calcSingleCon(Value left, Value right, Operator op) {
        switch (op) {
            case Add -> {
                //a+0
                if (isZero(left)) return right;
                if (isZero(right)) return left;
            }
            case Sub -> {
                //a-0
                if (isZero(right)) return left;
            }
            case Mul -> {
                //a*0,a*1
                if (isZero(left) || isZero(right)) return new ConstInt(0);
                if (isOne(left)) return right;
                if (isOne(right)) return left;
            }
            case SDiv -> {
                //0/a,a/1
                if (isZero(left)) return new ConstInt(0);
                if (isOne(right)) return left;
            }
            case Mod -> {
                //0%a,a%1
                if (isZero(left) || isOne(right)
                        || (right instanceof ConstInt constInt && constInt.getVal() == -1)) {
                    return new ConstInt(0);
                }
            }
        }
        return null;
    }

    private Value calcNoneCon(Instruction instr) {
        BinaryInst cal = (BinaryInst) instr;
        Value left = cal.lVal();
        Value right = cal.rVal();
        Operator op = cal.getOp();
        switch (op) {
            case Add -> {
                if (left instanceof BinaryInst l) {
                    if (l.getOp() == Operator.Sub && sameValue(l.rVal(), right)) {
                        return l.lVal();
                    }
                }
                if (right instanceof BinaryInst r) {
                    if (r.getOp() == Operator.Sub && sameValue(left, r.rVal())) {
                        return r.lVal();
                    }
                }
                if (left instanceof BinaryInst l && right instanceof BinaryInst r) {
                    if ((l.getOp() == Operator.Add && r.getOp() == Operator.Sub)
                            || (l.getOp() == Operator.Sub && r.getOp() == Operator.Add)) {
                        if (sameValue(l.rVal(), r.rVal())) {
                            instr.deleteUse();
                            instr.addOperand(l.lVal());
                            instr.addOperand(r.lVal());
                        }
                    }
                }
            }
            case Sub -> {
                if (sameValue(left, right)) {
                    return new ConstInt(0);
                }
                if (left instanceof BinaryInst l) {
                    if (l.getOp() == Operator.Add) {
                        if (sameValue(l.lVal(), right)) {
                            return l.rVal();
                        }
                        if (sameValue(l.rVal(), right)) {
                            return l.rVal();
                        }
                    }
                }
                if (right instanceof BinaryInst r) {
                    if (r.getOp() == Operator.Sub && sameValue(left, r.lVal())) {
                        return r.rVal();
                    }
                }
            }
            case SDiv -> {
                if (sameValue(left, right)) {
                    return new ConstInt(1);
                }
            }
            case Mod -> {
                if (sameValue(left, right)) {
                    return new ConstInt(0);
                }
            }
        }
        return null;
    }

    private boolean sameValue(Value a, Value b) {
        if (a instanceof ConstInt) {
            return (b instanceof ConstInt) && ((ConstInt) a).getVal() == ((ConstInt) b).getVal();
        }
        return a == b;
    }

    private boolean isZero(Value value) {
        return (value instanceof ConstInt constInt && constInt.getVal() == 0);
    }

    private boolean isOne(Value value) {
        return (value instanceof ConstInt constInt && constInt.getVal() == 1);
    }

    private String getGVNHash(Instruction instr) {
        if (instr instanceof BinaryInst binaryInst) {
            String lVal = binaryInst.lVal().getName();
            String rVal = binaryInst.rVal().getName();
            String op = binaryInst.getOp().toString();
            if (binaryInst.getOp() == Operator.Add || binaryInst.getOp() == Operator.Mul) {
                if (lVal.compareTo(rVal) > 0) {
                    return rVal + ' ' + op + ' ' + lVal;
                }
            }
            return lVal + ' ' + op + ' ' + rVal;
        } else if (instr instanceof CallInst callInst) {
            if (callInst.getFunction().isLibrary() || callInst.getFunction().hasSideEffect()) {
                return null;
            }
            return callInst.getCallee();
        } else if (instr instanceof GEPInst gepInst) {
            StringBuilder res = new StringBuilder();
            res.append(gepInst.getPointer().getName());
            for (Value value : gepInst.getIndices()) {
                res.append(", ").append(value.getName());
            }
            return res.toString();
        }
        return null;
    }
}
