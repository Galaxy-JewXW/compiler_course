package pass;

import IR.IRModule;
import IR.types.IntegerType;
import IR.values.*;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.AllocInst;
import IR.values.instructions.men.LoadInst;
import IR.values.instructions.men.PhiInst;
import IR.values.instructions.men.StoreInst;
import pass.Analysis.DomAnalysis;

import java.util.*;

public class Mem2Reg {
    private final IRModule irModule;
    private Instruction curAlloc;
    private HashSet<Instruction> useInstr;
    private HashSet<Instruction> defInstr;
    private HashSet<BasicBlock> defBB;
    private Stack<Value> reachDef;

    public Mem2Reg(IRModule irModule) {
        this.irModule = irModule;
        DomAnalysis.getInstance().analyze(irModule);
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            BasicBlock entry = function.getBasicBlocks().get(0);
            for (BasicBlock bb : function.getBasicBlocks()) {
                for (Instruction instr : new ArrayList<>(bb.getInstructions())) {
                    if (instr instanceof AllocInst allocInst && allocInst.getAllocType() == IntegerType.i32) {
                        init(instr);
                        insertPhi();
                        rename(entry);
                    }
                }
            }
        }
    }

    private void init(Instruction instr) {
        this.curAlloc = instr;
        this.useInstr = new HashSet<>();
        this.defInstr = new HashSet<>();
        this.defBB = new HashSet<>();
        this.reachDef = new Stack<>();
        for (Use use : instr.getUseList()) {
            Instruction user = (Instruction) use.getUser();
            if (user instanceof StoreInst) {
                defInstr.add(user);
                defBB.add(user.getBasicBlock());
            } else if (user instanceof LoadInst) {
                useInstr.add(user);
            }
        }
    }

    private void insertPhi() {
        HashSet<BasicBlock> f = new HashSet<>();
        Stack<BasicBlock> w = new Stack<>();
        for (BasicBlock bb : defBB) {
            w.push(bb);
        }
        while (!w.isEmpty()) {
            BasicBlock x = w.pop();
            for (BasicBlock y : x.getDF()) {
                if (!f.contains(y)) {
                    insertPhiToEntry(y);
                    f.add(y);
                    if (!defBB.contains(y)) {
                        w.push(y);
                    }
                }
            }
        }
    }

    private void insertPhiToEntry(BasicBlock basicBlock) {
        PhiInst phi = new PhiInst();
        phi.setBasicBlock(basicBlock);
        basicBlock.getInstructions().add(0, phi);
        useInstr.add(phi);
        defInstr.add(phi);
    }

    private void rename(BasicBlock entry) {
        int cnt = 0;
        Iterator<Instruction> it = entry.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instr = it.next();
            if (instr == curAlloc) {
                it.remove();
            } else if (instr instanceof StoreInst && defInstr.contains(instr)) {
                reachDef.push(((StoreInst) instr).getVal());
                cnt++;
                instr.deleteUse();
                it.remove();
            } else if (instr instanceof LoadInst && useInstr.contains(instr)) {
                Value newVal = (reachDef.isEmpty()) ? new NullValue() : reachDef.peek();
                instr.replacedByNewVal(newVal);
                instr.deleteUse();
                it.remove();
            } else if (instr instanceof PhiInst && defInstr.contains(instr)) {
                reachDef.push(instr);
                cnt++;
            }
        }
        for (BasicBlock bb : entry.getNext()) {
            Instruction first = bb.getInstructions().get(0);
            if (first instanceof PhiInst phiInst && useInstr.contains(first)) {
                Value newVal = (reachDef.isEmpty()) ? new NullValue() : reachDef.peek();
                phiInst.addValue(entry, newVal);
            }
        }
        for (BasicBlock bb : entry.getImmDomList()) {
            rename(bb);
        }
        for (int i = 0; i < cnt; i++) {
            reachDef.pop();
        }
    }
}
