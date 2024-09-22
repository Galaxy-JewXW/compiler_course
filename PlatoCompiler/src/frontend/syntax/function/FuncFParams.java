package frontend.syntax.function;

import frontend.symbol.VarType;
import frontend.syntax.SyntaxNode;
import frontend.token.TokenType;

import java.util.ArrayList;

// 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
// 1.花括号内重复0次 2.花括号内重复多次
public class FuncFParams extends SyntaxNode {
    private final ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    @Override
    public void print() {
        for (int i = 0; i < funcFParams.size(); i++) {
            if (i > 0) {
                System.out.println(TokenType.printType(TokenType.COMMA));
            }
            funcFParams.get(i).print();
        }
        System.out.println("<FuncFParams>");
    }

    public ArrayList<VarType> getTypes() {
        ArrayList<VarType> types = new ArrayList<>();
        for (FuncFParam f : funcFParams) {
            VarType type = switch (f.getBType().getToken().getType()) {
                case INTTK -> VarType.INT;
                case CHARTK -> VarType.CHAR;
                default -> throw new RuntimeException("Shouldn't reach here");
            };
            types.add(type);
        }
        return types;
    }

    public ArrayList<Integer> getDims() {
        ArrayList<Integer> dims = new ArrayList<>();
        for (FuncFParam f : funcFParams) {
            if (f.isArray()) {
                dims.add(1);
            } else {
                dims.add(0);
            }
        }
        return dims;
    }
}
