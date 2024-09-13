package node.func;

import IO.OutputHandler;
import node.func.FuncFParam;
import token.TokenType;

import java.util.ArrayList;

public class FuncFParams {
    //FuncFParams → FuncFParam { ',' FuncFParam }
    private ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    public void print() {
        funcFParams.get(0).print();
        for (int i = 1; i < funcFParams.size(); i++) {
            OutputHandler.printToken(TokenType.COMMA);
            funcFParams.get(i).print();
        }
        OutputHandler.println("<FuncFParams>");
    }

    public void checkError() {
        for (FuncFParam funcFParam : funcFParams) {
            funcFParam.checkError();
        }
    }
}
