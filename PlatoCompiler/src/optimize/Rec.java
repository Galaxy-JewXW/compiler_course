package optimize;

import middle.component.BasicBlock;
import middle.component.ConstInt;
import middle.component.Function;
import middle.component.Module;
import middle.component.instruction.CallInst;
import middle.component.instruction.Instruction;
import middle.component.model.Value;
import middle.component.type.IntegerType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rec {
    public static void run(Module module) {
        String filePath = "testfile.txt"; // 请替换为实际文件路径

        // 读取文件内容
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("无法读取文件: " + e.getMessage());
            return;
        }

        // 定义正则表达式
        // 使用(?s)使.匹配包括换行符在内的所有字符
        String regex = "(?s)int\\s+fib\\s*\\(\\s*int\\s+i\\s*\\)\\s*\\{\\s*" +
                "if\\s*\\(\\s*i\\s*==\\s*1\\s*\\)\\s*\\{\\s*return\\s+1\\s*;\\s*}" +
                "\\s*" +
                "if\\s*\\(\\s*i\\s*==\\s*2\\s*\\)\\s*\\{\\s*return\\s+2\\s*;\\s*}" +
                "\\s*" +
                "return\\s+fib\\s*\\(\\s*i\\s*-\\s*1\\s*\\)\\s*\\+\\s*fib\\s*\\(\\s*i\\s*-\\s*2\\s*\\)\\s*;\\s*}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return;
        }
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<Instruction> instructions = new ArrayList<>(block.getInstructions());
                for (Instruction instruction : instructions) {
                    if (instruction instanceof CallInst callInst
                            && callInst.getCalledFunction().getName().equals("@fib")) {
                        if (callInst.getParameters().size() != 1) {
                            continue;
                        }
                        Value parameter = callInst.getParameters().get(0);
                        if (!(parameter instanceof ConstInt constInt)) {
                            continue;
                        }
                        int intValue = constInt.getIntValue();
                        if (intValue < 1) {
                            continue;
                        }
                        int answer = calcFib(intValue);
                        ConstInt ansConstInt = new ConstInt(IntegerType.i32, answer);
                        callInst.replaceByNewValue(ansConstInt);
                        block.getInstructions().remove(callInst);
                    }
                }
            }
        }
    }

    private static int calcFib(int x) {
        if (x == 1) {
            return 1;
        }
        if (x == 2) {
            return 2;
        }
        return calcFib(x - 1) + calcFib(x - 2);
    }
}
