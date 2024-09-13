package backend;

import IO.OutputHandler;
import IR.IRModule;
import IR.types.*;
import IR.values.*;
import IR.values.instructions.BinaryInst;
import IR.values.instructions.CallInst;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.GEPInst;
import IR.values.instructions.men.StoreInst;
import IR.values.instructions.men.AllocInst;
import IR.values.instructions.men.LoadInst;
import IR.values.instructions.terminator.BrInst;
import IR.values.instructions.terminator.RetInst;
import backend.Symbol.MipsSymbol;
import backend.Symbol.MipsSymbolTable;
import config.Config;
import pass.Analysis.MoveInst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MIPSGenerator {
    private static final MIPSGenerator mipsGenerator = new MIPSGenerator();

    public static MIPSGenerator getInstance() {
        return mipsGenerator;
    }

    private final MipsSymbolTable symbolTables = new MipsSymbolTable();
    private final List<Register> tmp_reg = Arrays.asList(Register.K0, Register.K1);
    //这里设计上选了v1作为特殊的能够保持持久的tmp_reg
    int tmp_ptr = 0;
    Function curFunc;

    public void genMIPS() {
        IRModule irModule = IRModule.getInstance();
        symbolTables.addSymbolTable();
        genMacro();
        OutputHandler.genMIPS(".data");
        for (GlobalVar globalVar : irModule.getGlobalVars()) {
            //getGp(globalVar.getName(), globalVar);
            genGlobalVar(globalVar);
        }
        OutputHandler.genMIPS(".text");
        jal("main");
        li(Register.V0, 10);
        syscall();
        for (Function function : irModule.getFunctions()) {
            curFunc = function;
            symbolTables.addSymbolTable();
            OutputHandler.genMIPS(function.getName() + ":");
            int rec = function.getArguments().size();
            int index = 1;
            for (Argument arg : function.getArguments()) {
                if (index >= 4) {
                    lw(Register.K0, Register.SP, 4 * rec);
                    getSp(arg.getName(), arg);
                    sw(Register.K0, Register.SP, getOff(arg.getName()));
                } else {
                    Register register = Register.index2reg(Register.A0.ordinal() + index);
                    function.getVar2reg().put(arg, register);
                }
                rec--;
                index++;
            }
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                OutputHandler.genMIPS(blockLabel(function, basicBlock) + ":");
                for (Instruction instruction : basicBlock.getInstructions()) {
                    if (!(instruction instanceof AllocInst)) {
                        getSp(instruction.getName(), instruction);
                    }
                    OutputHandler.genMIPS("#" + instruction);
                    translate(instruction);
                }
            }
            symbolTables.rmSymbolTable();
        }
    }

    private void genMacro() {
        genGetInt();
        genPutInt();
        genPutCh();
        genPutStr();
    }

    private void genGlobalVar(GlobalVar globalVar) {
        StringBuilder ans = new StringBuilder(globalVarLabel(globalVar.getName()));
        if (globalVar.getValue() instanceof ConstArray constArray) {
            if (constArray.allZero()) {
                ans.append(": .space ");
                int space = 4 * ((ArrayType) constArray.getType()).getCapacity();
                ans.append(space);
            } else {
                ans.append(": .word ");
                ArrayType arrayType = (ArrayType) ((PointerType) globalVar.getType()).getTargetType();
                ans.append(listGlobalArray(arrayType, constArray));
            }
        } else if (globalVar.getValue() instanceof ConstStr constStr) {
            ans.append(": .asciiz ");
            ans.append(constStr.getName());
        } else {
            ans.append(": .word ");
            ans.append(globalVar.getValue().getName());
        }
        OutputHandler.genMIPS(ans.toString());
    }

    private String listGlobalArray(ArrayType arrayType, ConstArray constArray) {
        StringBuilder res = new StringBuilder();
        for (Value value : constArray.getValues()) {
            if (value instanceof ConstArray) {
                res.append(listGlobalArray((ArrayType) arrayType.getElementType(),
                        (ConstArray) value));
            } else {
                res.append(value.getName()).append(',');
            }
        }
        int last = arrayType.getLen() - constArray.getValues().size();
        res.append("0,".repeat(Math.max(0, last)));
        return res.toString();
    }

    private String globalVarLabel(String name) {
        return name.substring(1);
    }

    private String blockLabel(Function function, BasicBlock basicBlock) {
        return function.getName() + '_' + basicBlock.getName();
    }

    private String blockLabel(BasicBlock basicBlock) {
        return curFunc.getName() + '_' + basicBlock.getName();
    }

    private void genGetInt() {
        OutputHandler.genMIPS(".macro getInt");
        li(Register.V0, 5);
        syscall();
        OutputHandler.genMIPS(".end_macro");
        //返回值在$v0
    }

    private void genPutInt() {
        //move("$a0", "%in");
        OutputHandler.genMIPS(".macro putInt");
        li(Register.V0, 1);
        syscall();
        OutputHandler.genMIPS(".end_macro");
    }

    private void genPutCh() {
        //move($a0,%in)
        OutputHandler.genMIPS(".macro putCh");
        li(Register.V0, 11);
        syscall();
        OutputHandler.genMIPS(".end_macro");
    }

    private void genPutStr() {
        //move($a0,%addr)
        OutputHandler.genMIPS(".macro putStr");
        li(Register.V0, 4);
        syscall();
        OutputHandler.genMIPS(".end_macro");
    }

    private void translate(Instruction ir) {
        if (ir instanceof AllocInst allocInst) {
            parseAllocInst(allocInst);
        } else if (ir instanceof GEPInst gepInst) {
            parseGepInst(gepInst);
        } else if (ir instanceof LoadInst loadInst) {
            parseLoadInst(loadInst);
        } else if (ir instanceof StoreInst storeInst) {
            parseStoreInst(storeInst);
        } else if (ir instanceof BrInst brInst) {
            parseBrInst(brInst);
        } else if (ir instanceof RetInst retInst) {
            parseRet(retInst);
        } else if (ir instanceof BinaryInst binaryInst) {
            parseBinary(binaryInst);
        } else if (ir instanceof CallInst callInst) {
            parseCall(callInst);
        } else if (ir instanceof MoveInst moveInst) {
            parseMoveInst(moveInst);
        }
    }

    private void parseAllocInst(AllocInst allocInst) {
        //指向int32
        Type type = allocInst.getType();
        Type target = ((PointerType) type).getTargetType();
        if (target instanceof IntegerType) {
            getSp(allocInst.getName(), allocInst);
        } else if (target instanceof ArrayType arrayType) {
            getSpArray(allocInst.getName(), 4 * arrayType.getCapacity(), allocInst);
        } else if (target instanceof PointerType pointerType) {
            if (pointerType.getTargetType() instanceof IntegerType) {
                getSp(allocInst.getName(), allocInst);
            } else if (pointerType.getTargetType() instanceof ArrayType arrayType) {
                getSpArray(allocInst.getName(), 4 * arrayType.getCapacity(), allocInst);
            }
        }
    }

    private void parseGepInst(GEPInst gepInst) {
        Value pointer = gepInst.getPointer();
        Type type = pointer.getType();
        Type target = ((PointerType) type).getTargetType();
        if (target instanceof ArrayType arrayType && arrayType.getElementType() == IntegerType.i8) {
            return;
        }
        ArrayList<Value> indices = gepInst.getIndices();
        Register keep = Register.V1;
        assign(keep, pointer);
        int immOff = 0;
        for (Value value : indices) {
            int base;
            if (target instanceof ArrayType) {
                base = ((ArrayType) target).getCapacity() * 4;
            } else {
                base = 4;
            }
            if (value instanceof ConstInt) {
                immOff += ((ConstInt) value).getVal() * base;
            } else {
                Register reg1 = getTmpReg();
                li(reg1, base);
                Register reg2 = load(value);
                OutputHandler.genMIPS(String.format("mult %s, %s", reg1, reg2));//k1*reg
                mflo(reg1);
                OutputHandler.genMIPS(String.format("addu %s, %s, %s", keep, keep, reg1));
            }
            if (target instanceof ArrayType arrayType) {
                target = arrayType.getElementType();
            }
        }
        if (immOff != 0) {
            OutputHandler.genMIPS(String.format("addiu %s, %s, %d", keep, keep, immOff));
        }
        store(keep, gepInst);
    }

    private void parseLoadInst(LoadInst loadInst) {
        if (loadInst.pointer() instanceof GEPInst) {
            Register reg = load(loadInst.pointer());
            Register tmp = getTmpReg();
            lw(tmp, reg, 0);
            store(tmp, loadInst);
        } else {
            Register reg = load(loadInst.pointer());
            store(reg, loadInst);
        }
    }

    private void parseStoreInst(StoreInst storeInst) {
        if (storeInst.getPtr() instanceof GEPInst) {
            Register reg1 = load(storeInst.getVal());
            Register reg2 = load(storeInst.getPtr());
            sw(reg1, reg2, 0);
        } else {
            Register reg = load(storeInst.getVal());
            store(reg, storeInst.getPtr());
        }
    }

    private void parseBrInst(BrInst brInst) {
        if (brInst.noneCond()) {
            j(brInst);
        } else {
            Register reg = load(brInst.getCond());
            blez(reg, blockLabel(brInst.getFalseBlock()));
            j(brInst);
        }
    }

    private void parseRet(RetInst retInst) {
        if (!(retInst.getType() == VoidType.voidType)) {
            assign(Register.V0, retInst.getOperands().get(0));
        }
        jr(Register.RA);
    }

    private void parseBinary(BinaryInst binaryInst) {
        Value lVal = binaryInst.lVal();
        Value rVal = binaryInst.rVal();
        switch (binaryInst.getOp()) {
            case Add -> add(binaryInst, lVal, rVal);
            case Sub -> sub(binaryInst, lVal, rVal);
            case Mul -> {
                if (Config.optimize) optimizeMul(binaryInst, lVal, rVal);
                else mul(binaryInst, lVal, rVal);
            }
            case SDiv -> {
                if (Config.optimize) optimizeDiv(binaryInst, lVal, rVal);
                else div(binaryInst, lVal, rVal);
            }
            case Mod -> {
                if (Config.optimize) optimizeMod(binaryInst, lVal, rVal);
                else mod(binaryInst, lVal, rVal);
            }
            case Sgt -> cmp("sgt", binaryInst, lVal, rVal);
            case Sge -> cmp("sge", binaryInst, lVal, rVal);
            case Slt -> cmp("slt", binaryInst, lVal, rVal);
            case Sle -> cmp("sle", binaryInst, lVal, rVal);
            case Ne -> cmp("sne", binaryInst, lVal, rVal);
            case Eq -> cmp("seq", binaryInst, lVal, rVal);
        }
    }

    private void parseCall(CallInst callInst) {
        Function function = callInst.getFunction();
        if (function.isLibrary()) {
            switch (function.getName()) {
                case "getint" -> {
                    OutputHandler.genMIPS("getInt");
                    store(Register.V0, callInst);
                }
                case "putint" -> {
                    assign(Register.A0, callInst.getOperands().get(1));
                    OutputHandler.genMIPS("putInt");
                }
                case "putch" -> {
                    assign(Register.A0, callInst.getOperands().get(1));
                    OutputHandler.genMIPS("putCh");
                }
                case "putstr" -> {
                    assign(Register.A0, ((GEPInst) callInst.getOperands().get(1)).getPointer());
                    OutputHandler.genMIPS("putStr");
                }
            }
        } else {
            sw(Register.RA, Register.SP, spOff);
            ArrayList<Register> save = new ArrayList<>(new HashSet<>(curFunc.getVar2reg().values()));
            //保存寄存器的值
            int rec = 1;
            for (Register reg : save) {
                sw(reg, Register.SP, spOff - 4 * rec);
                rec++;
            }
            //压入参数
            for (int i = 1; i < callInst.getOperands().size(); i++) {
                Value arg = callInst.getOperands().get(i);
                if (i < 4) {
                    Register reg = Register.index2reg(Register.A0.ordinal() + i);
                    if (arg instanceof Argument && getReg(arg) != null && getReg(arg) != reg) {
                        lw(reg, Register.SP, spOff - 4 * (save.indexOf(getReg(arg)) + 1));
                    } else {
                        assign(reg, callInst.getOperands().get(i));
                    }
                } else {
                    Register reg;
                    if (arg instanceof Argument && getReg(arg) != null) {
                        reg = Register.K0;
                        lw(reg, Register.SP, spOff - 4 * (save.indexOf(getReg(arg)) + 1));
                    } else {
                        reg = load(callInst.getOperands().get(i));
                    }
                    sw(reg, Register.SP, spOff - 4 * rec);

                }
                rec++;
            }
            OutputHandler.genMIPS(String.format("addiu $sp, $sp, %d", spOff - 4 * rec));
            jal(function.getName());
            //OutputHandler.genMIPS("nop");关闭延迟槽
            OutputHandler.genMIPS(String.format("addiu $sp, $sp, %d", -spOff + 4 * rec));
            lw(Register.RA, Register.SP, spOff);
            rec = 1;
            for (Register reg : save) {
                lw(reg, Register.SP, spOff - 4 * rec);
                rec++;
            }
            if (!(callInst.getType() == VoidType.voidType)) {
                store(Register.V0, callInst);
            }
        }
    }

    private void parseMoveInst(MoveInst moveInst) {
        Register reg1 = load(moveInst.getVal());
        store(reg1, moveInst.getTarget());
    }

    private void li(Register reg, String val) {
        String ans = String.format("li %s, %s", reg, val);
        OutputHandler.genMIPS(ans);
    }

    private void li(Register reg, int val) {
        String ans = String.format("li %s, %d", reg, val);
        OutputHandler.genMIPS(ans);
    }

    private void la(Register reg, String val) {
        OutputHandler.genMIPS(String.format("la %s, %s", reg, val));
    }

    private void syscall() {
        OutputHandler.genMIPS("syscall");
    }

    private void jal(String func) {
        OutputHandler.genMIPS("jal " + func);
    }

    private void j(BrInst brInst) {
        BasicBlock bb = brInst.getBasicBlock();
        ArrayList<BasicBlock> bbs = curFunc.getBasicBlocks();
        int index = bbs.indexOf(bb);
        if (index + 1 < bbs.size()) {
            if (bbs.get(index + 1) == brInst.getTrueBlock()) {
                return;
            }
        }
        OutputHandler.genMIPS("j " + blockLabel(brInst.getTrueBlock()));
    }

    private void jr(Register reg) {
        OutputHandler.genMIPS(String.format("jr %s", reg));
    }

    private void blez(Register reg, String label) {
        OutputHandler.genMIPS("blez " + reg + ", " + label);
    }

    private void move(Register reg, Register valReg) {
        if (reg == valReg) {
            return;
        }
        OutputHandler.genMIPS(String.format("move %s, %s", reg, valReg));
    }

    private void add(Value ans, Value lVal, Value rVal) {
        Register reg1 = load(lVal);
        Register reg2 = load(rVal);
        Register target = getReg(ans);
        boolean inSp = false;
        if (target == null) {
            inSp = true;
            target = getTmpReg();
        }
        OutputHandler.genMIPS(String.format("addu %s, %s, %s", target, reg1, reg2));
        if (inSp) {
            store(target, ans);
        }
    }

    private void sub(Value ans, Value lVal, Value rVal) {
        Register reg1 = load(lVal);
        Register reg2 = load(rVal);
        Register target = getReg(ans);
        boolean inSp = false;
        if (target == null) {
            inSp = true;
            target = getTmpReg();
        }
        OutputHandler.genMIPS(String.format("subu %s, %s, %s", target, reg1, reg2));
        if (inSp) {
            store(target, ans);
        }
    }

    private void mul(Value ans, Value lVal, Value rVal) {
        Register reg1 = load(lVal);
        Register reg2 = load(rVal);
        Register target = getReg(ans);
        boolean inSp = false;
        if (target == null) {
            inSp = true;
            target = getTmpReg();
        }
        OutputHandler.genMIPS(String.format("mult %s, %s", reg1, reg2));
        mflo(target);
        if (inSp) {
            store(target, ans);
        }
    }

    private void optimizeMul(Value ans, Value lVal, Value rVal) {
        if (!(lVal instanceof ConstInt || rVal instanceof ConstInt)) {
            mul(ans, lVal, rVal);
            return;
        }
        Value value;
        int imm;
        if (lVal instanceof ConstInt constInt) {
            imm = constInt.getVal();
            value = rVal;
        } else {
            imm = ((ConstInt) rVal).getVal();
            value = lVal;
        }
        int abs = (imm >= 0) ? imm : -imm;
        Register ansReg = Register.V1;
        if (getReg(ans) != null) {
            ansReg = getReg(ans);
        }
        Register reg = load(value);
        if (imm == -1) {
            OutputHandler.genMIPS("negu " + ansReg + ", " + reg);
        } else if ((abs & (abs - 1)) == 0) {
            OutputHandler.genMIPS(String.format("sll %s, %s, %d", ansReg, reg, getCTZ(abs)));
            if (imm < 0) {
                OutputHandler.genMIPS("negu " + ansReg + ", " + ansReg);
            }
        } else if (((abs - 1) & (abs - 2)) == 0) {
            OutputHandler.genMIPS(String.format("sll %s, %s, %d", Register.K0, reg, getCTZ(abs - 1)));
            OutputHandler.genMIPS(String.format("addu %s, %s, %s", ansReg, Register.K0, reg));
            if (imm < 0) {
                OutputHandler.genMIPS("negu " + ansReg + ", " + ansReg);
            }
        } else if (((abs + 1) & abs) == 0) {
            OutputHandler.genMIPS(String.format("sll %s, %s, %d", Register.K0, reg, getCTZ(abs + 1)));
            if (imm < 0) {
                OutputHandler.genMIPS(String.format("subu %s, %s, %s", ansReg, reg, Register.K0));
            } else {
                OutputHandler.genMIPS(String.format("subu %s, %s, %s", ansReg, Register.K0, reg));
            }
        } else if (count1(abs) == 2) {
            int r1 = getLow1(abs);
            int r2 = getCTZ(abs);
            OutputHandler.genMIPS(String.format("sll %s, %s, %d", Register.K0, reg, r1));
            OutputHandler.genMIPS(String.format("sll %s, %s, %d", Register.K1, reg, r2));
            OutputHandler.genMIPS(String.format("addu %s, %s, %s", ansReg, Register.K0, Register.K1));
            if (imm < 0) {
                OutputHandler.genMIPS("negu " + ansReg + ", " + ansReg);
            }
        } else {
            mul(ans, lVal, rVal);
            return;
        }
        if (getReg(ans) == null) store(ansReg, ans);
    }

    private int getCTZ(int num) {
        int r = 0;
        num >>>= 1;
        while (num > 0) {
            num >>>= 1;
            r++;
        }
        return r;
    }

    private int count1(int num) {
        int r = 0;
        while (num > 0) {
            r += num & 1;
            num >>>= 1;
        }
        return r;
    }

    private int getLow1(int num) {
        int r = 0;
        while (num > 0) {
            if ((num & 1) == 1) {
                return r;
            }
            num >>>= 1;
            r++;
        }
        return -1;
    }

    private void optimizeDiv(Value ans, Value lVal, Value rVal) {
        if (!(lVal instanceof ConstInt || rVal instanceof ConstInt)) {
            div(ans, lVal, rVal);
            return;
        }
        Register ansReg = Register.V1;
        if (getReg(ans) != null) {
            ansReg = getReg(ans);
        }
        optimizeDiv(ansReg, lVal, rVal);
        if (getReg(ans) == null) store(ansReg, ans);
    }

    private void optimizeDiv(Register ansReg, Value lVal, Value rVal) {
        Value value;
        int imm;
        if (lVal instanceof ConstInt constInt) {
            imm = constInt.getVal();
            value = rVal;
        } else {
            imm = ((ConstInt) rVal).getVal();
            value = lVal;
        }
        int abs = (imm >= 0) ? imm : -imm;
        Register reg = load(value);
        if (imm == -1) {
            OutputHandler.genMIPS("negu " + ansReg + ", " + reg);
        } else if ((abs & (abs - 1)) == 0) {
            // (n + ((n >> 31) >>> (32 - l))) >> l
            int l = getCTZ(abs);
            OutputHandler.genMIPS(String.format("sra %s, %s, %d", Register.K0, reg, 31));
            OutputHandler.genMIPS(String.format("srl %s, %s, %d", Register.K0, Register.K0, 32 - l));
            OutputHandler.genMIPS(String.format("addu %s, %s, %s", Register.K0, Register.K0, reg));
            OutputHandler.genMIPS(String.format("sra %s, %s, %d", ansReg, Register.K0, l));
        } else {
            long[] res = multiplier(abs);
            long m = res[0];
            int sh = (int) res[1];
            if (m <= 2147483647) {
                OutputHandler.genMIPS("li " + Register.K0 + ", " + m);
                OutputHandler.genMIPS("mult " + reg + ", " + Register.K0);
                OutputHandler.genMIPS("mfhi " + Register.K0);
            } else {
                OutputHandler.genMIPS("li " + Register.K0 + ", " + (m - (1L << 32)));
                OutputHandler.genMIPS("mult " + reg + ", " + Register.K0);
                OutputHandler.genMIPS("mfhi " + Register.K0);
                OutputHandler.genMIPS(String.format("addu %s, %s, %s", Register.K0, Register.K0, reg));
            }
            OutputHandler.genMIPS(String.format("sra %s, %s, %d", Register.K0, Register.K0, sh));
            OutputHandler.genMIPS(String.format("srl %s, %s, %d", Register.K1, reg, 31));
            OutputHandler.genMIPS(String.format("addu %s, %s, %s", ansReg, Register.K0, Register.K1));
        }
    }

    private void optimizeMod(Value ans, Value lVal, Value rVal) {
        if (!(lVal instanceof ConstInt || rVal instanceof ConstInt)) {
            mod(ans, lVal, rVal);
            return;
        }
        Value value;
        Value immValue;
        if (lVal instanceof ConstInt) {
            value = rVal;
            immValue = lVal;
        } else {
            value = lVal;
            immValue = rVal;
        }
        Register ansReg = Register.V1;
        if (getReg(ans) != null) {
            ansReg = getReg(ans);
        }
        Register reg = load(value);
        if (reg == ansReg) {
            OutputHandler.genMIPS("move " + Register.V0 + ", " + reg);
            reg = Register.V0;
        }
        optimizeDiv(ans, lVal, rVal);
        optimizeMul(ans, ans, immValue);
        OutputHandler.genMIPS(String.format("sub %s, %s, %s", ansReg, reg, ansReg));
        if (getReg(ans) == null) store(ansReg, ans);
    }

    private long[] multiplier(int d) {
        long nc = (1L << 31) - (1L << 31) % d - 1;
        long p = 32;
        while ((1L << p) <= nc * (d - (1L << p) % d)) {
            p++;
        }
        long m = ((1L << p) + (long) d - (1L << p) % d) / (long) d;
        long n = ((m << 32) >>> 32);
        return new long[]{n, p - 32};
    }

    private void div(Value ans, Value lVal, Value rVal) {
        Register reg1 = load(lVal);
        Register reg2 = load(rVal);
        Register target = getReg(ans);
        boolean inSp = false;
        if (target == null) {
            inSp = true;
            target = getTmpReg();
        }
        OutputHandler.genMIPS(String.format("div %s, %s", reg1, reg2));
        mflo(target);
        if (inSp) {
            store(target, ans);
        }
    }

    private void mod(Value ans, Value lVal, Value rVal) {
        Register reg1 = load(lVal);
        Register reg2 = load(rVal);
        Register target = getReg(ans);
        boolean inSp = false;
        if (target == null) {
            inSp = true;
            target = getTmpReg();
        }
        OutputHandler.genMIPS(String.format("div %s, %s", reg1, reg2));
        mfhi(target);
        if (inSp) {
            store(target, ans);
        }
    }

    private void cmp(String ins, Value ans, Value lVal, Value rVal) {
        Register reg1 = load(lVal);
        Register reg2 = load(rVal);
        Register target = getReg(ans);
        boolean inSp = false;
        if (target == null) {
            inSp = true;
            target = getTmpReg();
        }
        OutputHandler.genMIPS(ins + String.format(" %s, %s, %s", target, reg1, reg2));
        if (inSp) {
            store(target, ans);
        }
    }

    private void assign(Register reg, Value value) {
        if (getReg(value) != null) {
            move(reg, getReg(value));
            return;
        }
        if (isNumber(value.getName())) {
            li(reg, value.getName());
        } else if (value instanceof GlobalVar globalVar) {
            la(reg, globalVarLabel(value.getName()));
            if (globalVar.getValue() instanceof ConstInt) {
                lw(reg, reg, 0);
            }
        } else {
            lw(reg, Register.SP, getOff(value.getName()));
        }
    }

    private Register load(Value value) {
        Register ans = getReg(value);
        if (ans != null) {
            return ans;
        }
        ans = getTmpReg();
        if (isNumber(value.getName())) {
            li(ans, value.getName());
        } else if (value instanceof GlobalVar globalVar) {
            la(ans, globalVarLabel(value.getName()));
            if (globalVar.getValue() instanceof ConstInt) {
                lw(ans, ans, 0);
            }
        } else {
            lw(ans, Register.SP, getOff(value.getName()));
        }
        return ans;
    }

    private void lw(Register reg, Register base, int off) {
        OutputHandler.genMIPS(String.format("lw %s, %s(%s)", reg, off, base));
    }

    private void store(Register reg, Value value) {
        if (getReg(value) != null) {
            move(getReg(value), reg);
            return;
        }
        if (value instanceof GlobalVar) {
            Register target = getTmpReg();
            la(target, globalVarLabel(value.getName()));
            sw(reg, target, 0);
        } else {
            sw(reg, Register.SP, getOff(value.getName()));
        }
    }

    private void sw(Register reg, Register base, int off) {
        OutputHandler.genMIPS(String.format("sw %s, %s(%s)", reg, off, base));
    }

    private void mfhi(Register reg) {
        OutputHandler.genMIPS("mfhi " + reg);
    }

    private void mflo(Register reg) {
        OutputHandler.genMIPS("mflo " + reg);
    }

    private boolean isNumber(String name) {
        return name.matches("-?\\d+");
    }

    int spOff = 0;

    /*
    栈的调用：spOff指向将要存的值，spOff+4指向上一个存入的的值
    */
    private void getSp(String name, Value value) {
        if (name.isEmpty() || symbolTables.contains(name) || getReg(value) != null) {
            return;
        }
        symbolTables.addSymbol(name, new MipsSymbol("$sp", spOff, value));
        spOff -= 4;
    }

    private void getSpArray(String name, int off, Value value) {
        if (name.isEmpty() || symbolTables.contains(name)) {
            return;
        }
        getSp(name, value);
        spOff -= off;
        OutputHandler.genMIPS("addiu $k0, $sp, " + (spOff + 4));
        store(Register.K0, value);
    }

    private int getOff(String name) {
        return symbolTables.getSymbol(name).getOff();
    }

    private Value getVal(String name) {
        return symbolTables.getSymbol(name).getVal();
    }

    private Register getReg(Value value) {
        if (curFunc.getVar2reg().containsKey(value)) return curFunc.getVar2reg().get(value);
        return null;
    }

    private Register getTmpReg() {
        tmp_ptr = 1 - tmp_ptr;
        return tmp_reg.get(tmp_ptr);
    }
}
