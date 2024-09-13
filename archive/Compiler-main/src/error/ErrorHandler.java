package error;

import IO.OutputHandler;
import node.LVal;
import node.expression.*;
import error.symbol.*;

import java.util.ArrayList;
import java.util.Collections;

public class ErrorHandler {
    private static final ErrorHandler errorHandler = new ErrorHandler();

    public static ErrorHandler getInstance() {
        return errorHandler;
    }

    private final ArrayList<ErrorNode> errorNodes = new ArrayList<>();
    private final ArrayList<SymbolTables> symbolTables = new ArrayList<>();
    private int loopLev = 0;

    public void addError(ErrorNode errorNode) {
        errorNodes.add(errorNode);
    }

    public void logErrors() {
        Collections.sort(errorNodes);
        for (ErrorNode errorNode : errorNodes) {
            OutputHandler.logError(errorNode);
        }
    }

    public boolean hasError() {
        return !errorNodes.isEmpty();
    }

    public void addSymbolTable(Type type) {
        symbolTables.add(new SymbolTables(type));
    }

    public void removeSymbolTable() {
        symbolTables.remove(symbolTables.size() - 1);
    }

    public void addSymbol(Symbol symbol) {
        symbolTables.get(symbolTables.size() - 1).addSymbol(symbol);
    }

    public Symbol getSymbol(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).contains(name)) {
                return symbolTables.get(i).get(name);
            }
        }
        return null;
    }

    public boolean defined(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).contains(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCurTable(String name) {
        if (symbolTables.isEmpty()) {
            return false;
        }
        return symbolTables.get(symbolTables.size() - 1).contains(name);
    }

    public boolean curInFunc() {
        return symbolTables.get(symbolTables.size() - 1).isFunc();
    }

    public boolean inIntFunc() {
        return symbolTables.get(symbolTables.size() - 1).isIntFunc();
    }

    public boolean inVoidFunc() {
        return symbolTables.get(symbolTables.size() - 1).isVoidFunc();
    }

    public void addLoopLev() {
        this.loopLev++;
    }

    public void minusLoopLev() {
        this.loopLev--;
    }

    public boolean isInLoop() {
        return this.loopLev != 0;
    }

    public boolean isCon(String name) {
        Symbol symbol = getSymbol(name);
        if (!(symbol instanceof VarSymbol)) {
            return false;
        }
        return ((VarSymbol) symbol).isCon();
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
            if (!(getSymbol(unaryExp.getIdent().getToken()) instanceof FuncSymbol)) {
                return null;
            }
            return new FuncParam(unaryExp.getIdent().getToken(), Type.INT, 0);
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
        return new FuncParam(lVal.getIdent().getToken(), Type.INT, lVal.getExps().size());
    }
}
