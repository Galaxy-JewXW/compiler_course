package pass;

import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.Instruction;

import java.util.HashSet;

public class GCM {
    private static HashSet<Instruction> visited = new HashSet<>();

    public static void build(Module module) {
        module.getFunctions().forEach(GCM::rearrange);
    }

    private static void rearrange(Function function) {
        visited = new HashSet<>();
    }

    private enum InstrType {
        MOVABLE, IMMOVABLE
    }
}
