package optimize;

import middle.Module;
import middle.component.Argument;
import middle.component.BasicBlock;
import middle.component.Function;
import middle.component.instructions.Instruction;
import middle.component.model.Value;

/*编号其实只用在输出中，所以只需要在输出前，
 *遍历每个 Function中的所有 Value，按顺序为它们分配编号即可。
 */
public class SlotTracker {
    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            renameFunction(function);
        }
    }

    private static void renameFunction(Function function) {
        Value.resetIdCount();
        for (Argument argument : function.getArguments()) {
            renameArgument(argument);
        }
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            renameBasicBlock(basicBlock);
        }
    }

    private static void renameArgument(Argument argument) {
        argument.setName("%" + Value.allocIdCount());
    }

    private static void renameBasicBlock(BasicBlock basicBlock) {
        basicBlock.setName(String.valueOf(Value.allocIdCount()));
        for (Instruction instruction : basicBlock.getInstructions()) {
            renameInstruction(instruction);
        }
    }

    private static void renameInstruction(Instruction instruction) {
        if (!instruction.getName().isEmpty()) {
            instruction.setName("%" + Value.allocIdCount());
        }
    }
}
