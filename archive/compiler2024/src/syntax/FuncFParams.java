package syntax;

import frontend.TokenType;

import java.util.ArrayList;

// 函数形参表 FuncFParams -> FuncFParam { ',' FuncFParam }
public class FuncFParams {
    private final ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<FuncFParam> getParams() {
        return funcFParams;
    }

    public void output() {
        funcFParams.get(0).output();
        for (int i = 1; i < funcFParams.size(); i++) {
            System.out.println(TokenType.COMMA.name() + " " + TokenType.COMMA);
            funcFParams.get(i).output();
        }
        System.out.println("<FuncFParams>");
    }

    public void check() {
        for (FuncFParam funcFParam : funcFParams) {
            funcFParam.check();
        }
    }
}
