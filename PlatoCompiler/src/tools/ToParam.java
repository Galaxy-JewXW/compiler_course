package tools;

import frontend.TableManager;
import frontend.symbol.FuncParam;
import frontend.symbol.FuncSymbol;
import frontend.symbol.Symbol;
import frontend.symbol.SymbolType;
import frontend.symbol.VarSymbol;
import frontend.syntax.LVal;
import frontend.syntax.expression.AddExp;
import frontend.syntax.expression.Exp;
import frontend.syntax.expression.PrimaryExp;
import frontend.syntax.expression.UnaryExp;

/**
 * 工具类，用于将Exp转换为FuncParam
 * 用于函数调用时参数类型是否匹配的检查
 * 对于函数调用的检查，只需要检测exp的第一个子表达式，
 * 即可判断其类型是否符合要求
 */
public class ToParam {
    public static FuncParam expToParam(Exp exp) {
        return addExpToParam(exp.getAddExp());
    }

    private static FuncParam addExpToParam(AddExp addExp) {
        return unaryExpToParam(addExp.getMulExps().get(0).getUnaryExps().get(0));
    }

    private static FuncParam unaryExpToParam(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            return primaryExpToParam(unaryExp.getPrimaryExp());
        } else if (unaryExp.getIdent() != null) {
            TableManager tableManager = TableManager.getInstance();
            Symbol symbol = tableManager.getSymbol(unaryExp.getIdent().getContent());
            if (symbol instanceof FuncSymbol funcSymbol) {
                return new FuncParam(unaryExp.getIdent().getContent(),
                        funcSymbol.getType(), 0);
            } else {
                throw new RuntimeException("Unrecognized symbol: " +
                        unaryExp.getIdent().getContent());
            }
        } else {
            return unaryExpToParam(unaryExp.getUnaryExp());
        }
    }

    private static FuncParam primaryExpToParam(PrimaryExp primaryExp) {
        if (primaryExp.getLVal() != null) {
            return lValToParam(primaryExp.getLVal());
        } else if (primaryExp.getExp() != null) {
            return expToParam(primaryExp.getExp());
        } else if (primaryExp.getNumber() != null) {
            return new FuncParam(null, SymbolType.INT, 0);
        } else {
            return new FuncParam(null, SymbolType.CHAR, 0);
        }
    }

    private static FuncParam lValToParam(LVal lval) {
        TableManager tableManager = TableManager.getInstance();
        VarSymbol symbol = (VarSymbol) tableManager.getSymbol(lval.getIdent().getContent());
        return new FuncParam(
                lval.getIdent().getContent(),
                symbol.getType(),
                lval.getExp() == null ? 0 : 1
        );
    }
}
