package backend.utils;

import middle.component.Module;
import middle.component.*;
import middle.component.instruction.Instruction;
import middle.component.instruction.PhiInst;
import middle.component.model.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ActiveVariable {
    public static void build(Module module) {
        module.getFunctions().forEach(ActiveVariable::analyzeFunction);
    }

    private static void analyzeFunction(Function func) {
        HashMap<BasicBlock, HashSet<Value>> inMap = new HashMap<>();
        HashMap<BasicBlock, HashSet<Value>> outMap = new HashMap<>();

        func.getBasicBlocks().forEach(block -> {
            analyzeSingleBlock(block);
            inMap.put(block, new HashSet<>());
            outMap.put(block, new HashSet<>());
        });

        boolean change;
        do {
            change = false;
            for (int i = func.getBasicBlocks().size() - 1; i >= 0; i--) {
                BasicBlock block = func.getBasicBlocks().get(i);
                HashSet<Value> newOutSet = calcOutSet(block, inMap);
                HashSet<Value> newInSet = calcInSet(block, newOutSet);

                if (!newInSet.equals(inMap.get(block)) || !newOutSet.equals(outMap.get(block))) {
                    change = true;
                    inMap.put(block, newInSet);
                    outMap.put(block, newOutSet);
                }
            }
        } while (change);
        for (BasicBlock block : func.getBasicBlocks()) {
            block.setInSet(inMap.get(block));
            block.setOutSet(outMap.get(block));
        }
    }

    private static void analyzeSingleBlock(BasicBlock block) {
        HashSet<Value> use = new HashSet<>();
        HashSet<Value> def = new HashSet<>();

        block.getInstructions().stream()
                .filter(instr -> instr instanceof PhiInst)
                .flatMap(instr -> instr.getOperands().stream())
                .filter(ActiveVariable::isValid)
                .forEach(use::add);
        block.getInstructions().forEach(instr -> {
            instr.getOperands().stream()
                    .filter(value -> !def.contains(value) && isValid(value))
                    .forEach(use::add);

            if (!use.contains(instr) && !instr.getName().isEmpty()) {
                def.add(instr);
            }
        });

        block.setUseSet(use);
        block.setDefSet(def);
    }

    private static boolean isValid(Value value) {
        return value instanceof Instruction || value instanceof FuncParam || value instanceof GlobalVar;
    }

    private static HashSet<Value> calcOutSet(BasicBlock block, HashMap<BasicBlock, HashSet<Value>> inMap) {
        return (HashSet<Value>) block.getNextBlocks().stream()
                .flatMap(child -> inMap.get(child).stream())
                .collect(Collectors.toSet());
    }

    private static HashSet<Value> calcInSet(BasicBlock block, HashSet<Value> outSet) {
        HashSet<Value> inSet = new HashSet<>(outSet);
        inSet.removeAll(block.getDefSet());
        inSet.addAll(block.getUseSet());
        return inSet;
    }
}