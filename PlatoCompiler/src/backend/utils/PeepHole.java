package backend.utils;

import backend.MipsFile;
import backend.enums.AsmOp;
import backend.enums.Register;
import backend.text.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PeepHole {
    public static void run() {
        MipsFile.getInstance().setInsect(false);
        removeJump();
        transfer2Move();
        removeMove();
        memPairRemoval();
        reverseCondBr();
        removeJump1();
        removeLiLa();
    }

    private static void removeJump() {
        ArrayList<TextAssembly> textAssemblies = MipsFile.getInstance().getTextSegment();
        List<TextAssembly> toRemove = IntStream.range(0, textAssemblies.size() - 1)
                .filter(i -> {
                    TextAssembly current = textAssemblies.get(i);
                    TextAssembly next = textAssemblies.get(i + 1);
                    return current instanceof JumpAsm jumpAsm
                            && next instanceof Label label
                            && jumpAsm.getTarget() != null
                            && jumpAsm.getTarget().equals(label.getLabel());
                })
                .mapToObj(textAssemblies::get)
                .toList();

        textAssemblies.removeAll(toRemove);
    }

    private static void transfer2Move() {
        ArrayList<TextAssembly> textAssemblies = MipsFile.getInstance().getTextSegment();
        IntStream.range(0, textAssemblies.size())
                .filter(i -> textAssemblies.get(i) instanceof CalcAsm)
                .forEach(i -> {
                    CalcAsm calcAsm = (CalcAsm) textAssemblies.get(i);
                    if (calcAsm.getOp().equals(AsmOp.ADDIU) && calcAsm.getImmediate() == 0) {
                        MoveAsm moveAsm = new MoveAsm(calcAsm.getRd(), calcAsm.getRs());
                        MipsFile.getInstance().getTextSegment().set(i, moveAsm);
                    }
                });
    }

    private static void removeMove() {
        List<TextAssembly> textSegment = MipsFile.getInstance().getTextSegment();

        // move $t1 <- $t1
        List<TextAssembly> toRemove1 = textSegment.stream()
                .filter(ta -> ta instanceof MoveAsm)
                .map(ta -> (MoveAsm) ta)
                .filter(moveAsm -> moveAsm.getDst().equals(moveAsm.getSrc()))
                .collect(Collectors.toList());
        textSegment.removeAll(toRemove1);

        // move $t1 <- $t2, move $t1 <- $t3
        List<TextAssembly> toRemove2 = IntStream.range(0, textSegment.size() - 1)
                .filter(i -> textSegment.get(i) instanceof MoveAsm
                        && textSegment.get(i + 1) instanceof MoveAsm)
                .filter(i -> {
                    MoveAsm moveAsm1 = (MoveAsm) textSegment.get(i);
                    MoveAsm moveAsm2 = (MoveAsm) textSegment.get(i + 1);
                    return moveAsm1.getDst().equals(moveAsm2.getDst());
                })
                .mapToObj(textSegment::get)
                .toList();
        textSegment.removeAll(toRemove2);

        // move $t1 <- $t2, move $t2 <- $t1
        List<TextAssembly> toRemove3 = IntStream.range(0, textSegment.size() - 1)
                .filter(i -> textSegment.get(i) instanceof MoveAsm
                        && textSegment.get(i + 1) instanceof MoveAsm)
                .filter(i -> {
                    MoveAsm moveAsm1 = (MoveAsm) textSegment.get(i);
                    MoveAsm moveAsm2 = (MoveAsm) textSegment.get(i + 1);
                    return moveAsm1.getDst().equals(moveAsm2.getSrc())
                            && moveAsm2.getDst().equals(moveAsm1.getSrc());
                })
                .mapToObj(i -> textSegment.get(i + 1))
                .toList();
        textSegment.removeAll(toRemove3);
    }

    private static void memPairRemoval() {
        ArrayList<TextAssembly> textAssemblies = MipsFile.getInstance().getTextSegment();
        IntStream.range(0, textAssemblies.size() - 1)
                .forEach(i -> {
                    TextAssembly textAssembly1 = textAssemblies.get(i);
                    TextAssembly textAssembly2 = textAssemblies.get(i + 1);
                    if (textAssembly1 instanceof MemAsm memAsm1 && textAssembly2 instanceof MemAsm memAsm2) {
                        if (memAsm1.getOp() == AsmOp.SW &&
                                memAsm2.getOp() == AsmOp.LW &&
                                memAsm1.getBase().equals(memAsm2.getBase()) &&
                                memAsm1.getOffset() == (memAsm2.getOffset())) {
                            MoveAsm moveAsm = new MoveAsm(memAsm2.getRd(), memAsm1.getRd());
                            textAssemblies.set(i + 1, moveAsm);
                        }
                    }
                });
    }

    // beq $t4, 1, main_b18  brAsm
    // j main_b23   jumpAsm
    // main_b18:   label
    private static void reverseCondBr() {
        ArrayList<TextAssembly> textAssemblies = new ArrayList<>(MipsFile.getInstance().getTextSegment());
        for (int i = 0; i < textAssemblies.size(); i++) {
            TextAssembly textAssembly = textAssemblies.get(i);
            if (!(textAssembly instanceof BrAsm brAsm)) {
                continue;
            }
            if (i + 1 < textAssemblies.size()
                    && textAssemblies.get(i + 1) instanceof JumpAsm jumpAsm) {
                if (i + 2 < textAssemblies.size()
                        && textAssemblies.get(i + 2) instanceof Label label) {
                    if (label.getLabel().equals(brAsm.getLabel())
                            && !brAsm.getLabel().equals(jumpAsm.getTarget())) {
                        AsmOp temp = switch (brAsm.getOp()) {
                            case BEQ -> AsmOp.BNE;
                            case BNE -> AsmOp.BEQ;
                            case BLE -> AsmOp.BGE;
                            case BGE -> AsmOp.BLE;
                            case BLT -> AsmOp.BGT;
                            case BGT -> AsmOp.BLT;
                            default -> throw new IllegalStateException(
                                    "Unexpected value: " + brAsm.getOp());
                        };
                        BrAsm brAsm1;
                        if (brAsm.getRt() == null) {
                            brAsm1 = new BrAsm(jumpAsm.getTarget(), brAsm.getRs(), temp, brAsm.getNumber());
                        } else {
                            brAsm1 = new BrAsm(jumpAsm.getTarget(), brAsm.getRs(), temp, brAsm.getRt());
                        }
                        MipsFile.getInstance().getTextSegment().set(i, brAsm1);
                        MipsFile.getInstance().getTextSegment().remove(jumpAsm);
                    }
                }
            }
        }
    }

    // j main_b7
    //
    // main_b4:
    //	# move %37 -> %3
    //	# br label %7
    //
    // main_b7:
    private static void removeJump1() {
        ArrayList<TextAssembly> textAssemblies = new ArrayList<>(
                MipsFile.getInstance().getTextSegment());
        for (int i = 0; i < textAssemblies.size(); i++) {
            TextAssembly textAssembly = textAssemblies.get(i);
            if (textAssembly instanceof JumpAsm jumpAsm) {
                boolean flag = false;
                String target = jumpAsm.getTarget();
                for (int j = i + 1; j < textAssemblies.size(); j++) {
                    TextAssembly textAssembly1 = textAssemblies.get(j);
                    if (textAssembly1 instanceof Label label
                            && label.getLabel().equals(target)) {
                        break;
                    }
                    if (!(textAssembly1 instanceof Label
                            || textAssembly1 instanceof Comment)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    MipsFile.getInstance().getTextSegment().remove(jumpAsm);
                }
            }
        }
    }

    private static void removeLiLa() {
        ArrayList<TextAssembly> toRemove = new ArrayList<>();
        HashMap<Register, Integer> liUses = new HashMap<>();
        HashMap<Register, String> laUses = new HashMap<>();
        for (TextAssembly asm : MipsFile.getInstance().getTextSegment()) {
            if (asm instanceof Label) {
                liUses.clear();
                laUses.clear();
                continue;
            }
            Register targetReg = null;

            if (asm instanceof LaAsm laAsm) {
                targetReg = laAsm.getTarget();
                String name = laAsm.getPointer();
                if (laUses.getOrDefault(targetReg, "").equals(name)) {
                    toRemove.add(asm);
                }
                laUses.put(targetReg, name);
                liUses.remove(targetReg);
                continue;
            }
            if (asm instanceof LiAsm liAsm) {
                targetReg = liAsm.getTarget();
                int value = liAsm.getImmediate();
                if (liUses.containsKey(targetReg) && liUses.get(targetReg).equals(value)) {
                    toRemove.add(asm);
                }
                liUses.put(targetReg, value);
                laUses.remove(targetReg);
                continue;
            }
            if (asm instanceof CalcAsm calcAsm) {
                targetReg = calcAsm.getRd();
            } else if (asm instanceof CmpAsm cmpAsm) {
                targetReg = cmpAsm.getRd();
            } else if (asm instanceof MemAsm memAsm) {
                if (memAsm.getOp() == AsmOp.LW) {
                    targetReg = memAsm.getRd();
                }
            } else if (asm instanceof SyscallAsm) {
                if (!liUses.containsKey(Register.V0)) {
                    throw new RuntimeException();
                } else {
                    if (liUses.get(Register.V0) == 5
                            || liUses.get(Register.V0) == 12) {
                        targetReg = Register.V0;
                    }
                }
            } else if (asm instanceof MoveAsm moveAsm) {
                targetReg = moveAsm.getDst();
            } else if (asm instanceof JumpAsm jumpAsm
                    && jumpAsm.getOp().equals(AsmOp.JAL)) {
                liUses.clear();
                laUses.clear();
            } else if (asm instanceof MDRegAsm mdRegAsm) {
                if (mdRegAsm.getOp().equals(AsmOp.MFHI)
                        || mdRegAsm.getOp().equals(AsmOp.MFLO)) {
                    targetReg = mdRegAsm.getRd();
                }
            }
            if (targetReg != null) {
                liUses.remove(targetReg);
                laUses.remove(targetReg);
            }
        }
        MipsFile.getInstance().getTextSegment().removeAll(toRemove);
    }
}
