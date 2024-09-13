package error;

import syntax.LVal;
import syntax.expression.*;
import error.symbol.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class ErrorVisitor {
    private static final ErrorVisitor ERROR_VISITOR = new ErrorVisitor();

    public static ErrorVisitor getInstance() {
        return ERROR_VISITOR;
    }

    private final ArrayList<ErrorLog> errorNodes = new ArrayList<>();
    private final Stack<ErrorTable> tableStack = new Stack<>();
    private int loopLevel = 0;

    public void addError(ErrorLog errorLog) {
        errorNodes.add(errorLog);
    }

    public void print(BufferedWriter writer) throws IOException {
        Collections.sort(errorNodes);
        for (ErrorLog errorLog : errorNodes) {
            writer.write(errorLog.toString() + "\n");
            writer.flush();
        }
        writer.close();
    }

    public boolean hasError() {
        return !errorNodes.isEmpty();
    }

    public void addTable(Type type) {
        tableStack.push(new ErrorTable(type));
    }

    public void removeTable() {
        tableStack.pop();
    }

    public void addSymbol(Symbol symbol) {
        tableStack.peek().addSymbol(symbol);
    }

    public Symbol getSymbol(String name) {
        for (int i = tableStack.size() - 1; i >= 0; i--) {
            if (tableStack.get(i).contains(name)) {
                return tableStack.get(i).getSymbol(name);
            }
        }
        return null;
    }

    public boolean symbolDefined(String name) {
        for (int i = tableStack.size() - 1; i >= 0; i--) {
            if (tableStack.get(i).contains(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean inCurrentTable(String name) {
        if (tableStack.isEmpty()) {
            return false;
        }
        return tableStack.peek().contains(name);
    }

    public boolean curInFunc() {
        return tableStack.peek().isFunc();
    }

    public boolean inIntFunc() {
        return tableStack.peek().isIntFunc();
    }

    public boolean inVoidFunc() {
        return tableStack.peek().isVoidFunc();
    }

    public void enterLoop() {
        loopLevel++;
    }

    public void exitLoop() {
        loopLevel--;
    }

    public boolean isInLoop() {
        return loopLevel != 0;
    }

    public boolean isConstant(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol instanceof VarSymbol varSymbol) {
            return varSymbol.isConstant();
        }
        return false;
    }

    public FuncParam expParam(Exp exp) {
        return addExpParam(exp.getAddExp());
    }

    public FuncParam addExpParam(AddExp addExp) {
        return mulExpParam(addExp.getMulExps().get(0));
    }

    public FuncParam mulExpParam(MulExp mulExp) {
        return unaryExpParam(mulExp.getUnaryExps().get(0));
    }

    public FuncParam unaryExpParam(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            return primaryExpParam(unaryExp.getPrimaryExp());
        } else if (unaryExp.getIdent() != null) {
            if (!(getSymbol(unaryExp.getIdent().getValue()) instanceof FuncSymbol)) {
                return null;
            }
            return new FuncParam(unaryExp.getIdent().getValue(), Type.INT, 0);
        } else {
            return unaryExpParam(unaryExp.getUnaryExp());
        }
    }

    public FuncParam primaryExpParam(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            return expParam(primaryExp.getExp());
        } else if (primaryExp.getlVal() != null) {
            return lValParam(primaryExp.getlVal());
        } else {
            return new FuncParam(null, Type.INT, 0);
        }
    }

    public FuncParam lValParam(LVal lVal) {
        return new FuncParam(lVal.getIdent().getValue(), Type.INT, lVal.getExps().size());
    }
}
