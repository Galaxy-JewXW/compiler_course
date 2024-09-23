package tools;

import java.util.ArrayList;

public class StrToArray {
    public static ArrayList<Integer> str2Array(String stringConst) {
        ArrayList<Integer> ans = new ArrayList<>();
        for (int i = 1; i < stringConst.length() - 1; i++) {
            if (stringConst.charAt(i) == '\\') {
                i++;
                int value = switch (stringConst.charAt(i)) {
                    case 'a' -> 7;
                    case 'b' -> 8;
                    case 't' -> 9;
                    case 'n' -> 10;
                    case 'v' -> 11;
                    case 'f' -> 12;
                    case '\"' -> 34;
                    case '\'' -> 39;
                    case '\\' -> 92;
                    case '0' -> 0;
                    default -> throw new RuntimeException("Invalid character '"
                            + stringConst.charAt(i) + "'");
                };
                ans.add(value);
                continue;
            }
            ans.add((int) stringConst.charAt(i));
        }
        // stringConst结尾有一个默认的0
        ans.add(0);
        return ans;
    }
}
