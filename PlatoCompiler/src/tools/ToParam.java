package tools;

import frontend.TableManager;
import frontend.symbol.*;
import frontend.symbol.ParamSymbol;
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
    public static ParamSymbol expToParam(Exp exp) {
        return addExpToParam(exp.getAddExp());
    }

    private static ParamSymbol addExpToParam(AddExp addExp) {
        return unaryExpToParam(addExp.getMulExps().get(0).getUnaryExps().get(0));
    }

    private static ParamSymbol unaryExpToParam(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            return primaryExpToParam(unaryExp.getPrimaryExp());
        } else if (unaryExp.getIdent() != null) {
            TableManager tableManager = TableManager.getInstance();
            Symbol symbol = tableManager.getSymbol(unaryExp.getIdent().getContent());
            if (symbol instanceof FuncSymbol funcSymbol) {
                return new ParamSymbol(unaryExp.getIdent().getContent(),
                        funcSymbol.getType(), 0);
            } else {
                throw new RuntimeException("Unrecognized symbol: " +
                        unaryExp.getIdent().getContent());
            }
        } else {
            return unaryExpToParam(unaryExp.getUnaryExp());
        }
    }

    private static ParamSymbol primaryExpToParam(PrimaryExp primaryExp) {
        if (primaryExp.getLVal() != null) {
            return lValToParam(primaryExp.getLVal());
        } else if (primaryExp.getExp() != null) {
            return expToParam(primaryExp.getExp());
        } else if (primaryExp.getNumber() != null) {
            return new ParamSymbol(null, SymbolType.INT, 0);
        } else {
            return new ParamSymbol(null, SymbolType.CHAR, 0);
        }
    }

    private static ParamSymbol lValToParam(LVal lval) {
        TableManager tableManager = TableManager.getInstance();
        VarSymbol symbol = (VarSymbol) tableManager.getSymbol(lval.getIdent().getContent());
        if (symbol == null) {
            return null;
        }
        return new ParamSymbol(
                lval.getIdent().getContent(),
                symbol.getType(),
                lval.getExp() == null ? 0 : 1
        );
    }
}
