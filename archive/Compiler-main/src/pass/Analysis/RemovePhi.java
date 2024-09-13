package pass.Analysis;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.NullValue;
import IR.values.Value;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.PhiInst;
import IR.values.instructions.terminator.BrInst;
import backend.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class RemovePhi {
    private final IRModule irModule;
    private Function curFunc;

    public RemovePhi(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            curFunc = function;
            phi2pc(function);
            pc2move(function);
        }
    }


    private void phi2pc(Function function) {
        HashMap<BasicBlock, PCInst> pcMap = new HashMap<>();
        for (BasicBlock bb : new ArrayList<>(function.getBasicBlocks())) {
            if (!(bb.getFirstInstr() instanceof PhiInst)) continue;
            for (BasicBlock prev : new ArrayList<>(bb.getPrev())) {
                PCInst pcInst = new PCInst();
                pcMap.put(prev, pcInst);
                if (prev.getNext().size() == 1) {
                    pcMap.put(prev, pcInst);
                    pcInst.setBasicBlock(prev);
                    prev.getInstructions().add(prev.getInstructions().size() - 1, pcInst);
                } else {
                    insertPC2Mid(function, prev, bb, pcInst);
                }
            }
            Iterator<Instruction> it = bb.getInstructions().iterator();
            while (it.hasNext()) {
                Instruction instr = it.next();
                if (instr instanceof PhiInst phiInst) {
                    for (int i = 0; i < phiInst.getOperands().size(); i++) {
                        pcMap.get(phiInst.getBBs().get(i)).addPair(
                                phiInst, phiInst.getOperands().get(i));
                    }
                    it.remove();
                }
            }
        }
    }

    private void insertPC2Mid(Function function, BasicBlock prev, BasicBlock next, PCInst pcInst) {
        BasicBlock mid = new BasicBlock();
        mid.giveName();
        mid.addInstruction(pcInst);
        function.getBasicBlocks().add(function.getBasicBlocks().indexOf(next), mid);
        BrInst brInst = (BrInst) prev.getLastInst();
        if (brInst.getTrueBlock() == next) {
            //这里并没有修改basicBlock的use关系，因为感觉没什么用处
            brInst.setTrueBLock(mid);
        } else {
            brInst.setFalseBlock(mid);
        }
        new BrInst(mid, next);//这里会自动加到mid的最后
        prev.getNext().remove(next);
        next.getPrev().remove(prev);
        prev.addNextBlock(mid);
        next.addPrevBlock(mid);
    }

    private void pc2move(Function function) {
        for (BasicBlock bb : function.getBasicBlocks()) {
            ArrayList<Instruction> instrList = bb.getInstructions();
            if (instrList.size() >= 2 && instrList.get(instrList.size() - 2) instanceof PCInst pcInst) {
                instrList.remove(instrList.size() - 2);
                for (MoveInst moveInst : convertPCInst(pcInst)) {
                    instrList.add(instrList.size() - 1, moveInst);
                    moveInst.setBasicBlock(bb);
                }
            }

        }
    }

    private ArrayList<MoveInst> convertPCInst(PCInst pcInst) {
        ArrayList<MoveInst> moveInsts = new ArrayList<>();
        ArrayList<Value> target = pcInst.getTargetPhi();
        ArrayList<Value> val = pcInst.getValOfPhi();
        HashMap<Value, Value> moveMap = new HashMap<>();
        HashSet<Register> visReg = new HashSet<>();
        HashSet<Value> visVal = new HashSet<>();
        for (int i = 0; i < target.size(); i++) {
            boolean conflict = false;
            boolean regConflict = false;
            if (visVal.contains(val.get(i))) {
                conflict = true;
            }
            visVal.add(target.get(i));
            if (getReg(val.get(i)) != null && visReg.contains(getReg(val.get(i)))) {
                regConflict = true;
            }
            if (getReg(target.get(i)) != null) {
                visReg.add(getReg(target.get(i)));
            }
            if (!moveMap.containsKey(val.get(i))) {
                if (conflict || regConflict) {
                    Value value = Value.newTmpValue(val.get(i).getName());
                    MoveInst moveInst = new MoveInst(value, val.get(i));
                    moveInsts.add(0, moveInst);
                    moveMap.put(val.get(i), value);
                }
            }
            if (moveMap.containsKey(val.get(i))) {
                moveInsts.add(new MoveInst(target.get(i), moveMap.get(val.get(i))));
            } else {
                moveInsts.add(new MoveInst(target.get(i), val.get(i)));
            }
        }
        return moveInsts;
    }

    private Register getReg(Value value) {
        return curFunc.getVar2reg().getOrDefault(value, null);
    }

    private boolean useSameReg(Value a, Value b) {
        return curFunc.getVar2reg().get(a) == curFunc.getVar2reg().get(b);
    }
}
