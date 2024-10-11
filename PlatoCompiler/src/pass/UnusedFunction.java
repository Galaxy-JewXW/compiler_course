package pass;

import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;

import java.util.*;
import java.util.stream.Collectors;

public class UnusedFunction {
    public static void run(Module module) {
        Set<Function> reachableFunctions = reachableFunctions(module);
        remove(module, reachableFunctions);
    }

    private static Set<Function> reachableFunctions(Module module) {
        Map<Function, Set<Function>> functionCallGraph = buildCallGraph(module);
        Function mainFunction = module.getFunctions().get(module.getFunctions().size() - 1);

        Set<Function> reachableFunctions = new HashSet<>();
        Queue<Function> functionQueue = new LinkedList<>();
        functionQueue.offer(mainFunction);

        while (!functionQueue.isEmpty()) {
            Function currentFunction = functionQueue.poll();
            if (reachableFunctions.add(currentFunction)) {
                functionQueue.addAll(functionCallGraph.getOrDefault(currentFunction, Collections.emptySet()));
            }
        }

        return reachableFunctions;
    }

    private static Map<Function, Set<Function>> buildCallGraph(Module module) {
        return module.getFunctions().stream()
                .collect(Collectors.toMap(
                        function -> function,
                        function -> function.getBasicBlocks().stream()
                                .flatMap(block -> block.getInstructions().stream())
                                .filter(instruction -> instruction instanceof CallInst)
                                .map(instruction -> ((CallInst) instruction).getCalledFunction())
                                .collect(Collectors.toSet())
                ));
    }

    private static void remove(Module module, Set<Function> reachableFunctions) {
        module.getFunctions().removeIf(function -> {
            if (!reachableFunctions.contains(function)) {
                cleanupFunction(function);
                return true;
            }
            return false;
        });
    }

    private static void cleanupFunction(Function function) {
        function.getBasicBlocks().forEach(block -> {
            block.getInstructions().forEach(Instruction::removeOperands);
            block.removeOperands();
        });
        function.removeOperands();
    }
}