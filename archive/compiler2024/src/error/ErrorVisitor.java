package error;

import syntax.LVal;
import syntax.expression.AddExp;
import syntax.expression.Exp;
import syntax.expression.MulExp;
import syntax.expression.PrimaryExp;
import syntax.expression.UnaryExp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ErrorVisitor {
    private static final ErrorVisitor errorVisitor = new ErrorVisitor();

    public static ErrorVisitor getInstance() {
        return errorVisitor;
    }

    private ErrorVisitor() {}

    private final ArrayList<ErrorLog> errorLogs = new ArrayList<>();
    private final ArrayList<ErrorSymbolTable> symbolTables = new ArrayList<>();
    private int loopLevel = 0;

    public void print(String path) throws IOException {
        Collections.sort(errorLogs);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (ErrorLog errorLog : errorLogs) {
            writer.write(errorLog.toString() + "\n");
            writer.flush();
        }
        writer.close();
    }

    public boolean hasError() {
        return !errorLogs.isEmpty();
    }

    public void addError(ErrorLog errorLog) {
        errorLogs.add(errorLog);
    }

    public void addTable(FuncParam.Type type) {
        symbolTables.add(new ErrorSymbolTable(type));
    }

    public void removeTable() {
        symbolTables.remove(symbolTables.size() - 1);
    }

    public void addSymbol(Symbol symbol) {
        symbolTables.get(symbolTables.size() - 1).add(symbol);
    }

    public Symbol getSymbol(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).contains(name)) {
                return symbolTables.get(i).get(name);
            }
        }
        return null;
    }

    public boolean symbolDefined(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).contains(name)) {
                return true;
            }
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
            return new FuncParam(unaryExp.getIdent().getValue(),
                    FuncParam.Type.INT, 0);
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
            return new FuncParam(null,
                    FuncParam.Type.INT, 0);
        }
    }

    public FuncParam lValParam(LVal lVal) {
        return new FuncParam(lVal.getIdent().getValue(),
                FuncParam.Type.INT, lVal.getExps().size());
    }

    public boolean inCurrentTable(String name) {
        if (symbolTables.isEmpty()) {
            return false;
        }
        return symbolTables.get(symbolTables.size() - 1).contains(name);
    }

    public boolean inIntFunc() {
        return symbolTables.get(symbolTables.size() - 1).isIntFunc();
    }

    public boolean inVoidFunc() {
        return symbolTables.get(symbolTables.size() - 1).isVoidFunc();
    }

    public boolean notInLoop() {
        return loopLevel == 0;
    }

    public void enterLoop() {
        loopLevel++;
    }

    public void exitLoop() {
        loopLevel--;
    }

    public boolean isConstant(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol instanceof VarSymbol varSymbol) {
            return varSymbol.isConstant();
        }
        return false;
    }
}
