package frontend;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.symbol.ConstSymbol;
import frontend.symbol.TableManager;
import frontend.symbol.VarType;
import frontend.syntax.CompUnit;
import frontend.syntax.Decl;
import frontend.syntax.expression.ConstExp;
import frontend.syntax.variable.ConstDecl;
import frontend.syntax.variable.ConstDef;
import frontend.syntax.variable.ConstInitVal;
import frontend.syntax.variable.VarDecl;
import frontend.token.TokenType;
import middle.component.InitialValue;
import middle.component.type.ArrayType;
import middle.component.type.IntegerType;
import middle.component.type.ValueType;

import java.util.ArrayList;

public class Visitor {
    private final TableManager tableManager = TableManager.getInstance();
    private final CompUnit compUnit;

    public Visitor(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void build() {
        visitCompUnit();
    }

    private void visitCompUnit() {
        tableManager.setGlobal(true);
        for (Decl decl : compUnit.getDecls()) {
            visitDecl(decl);
        }
    }

    private void visitDecl(Decl decl) {
        if (decl instanceof ConstDecl constDecl) {
            visitConstDecl(constDecl);
        } else if (decl instanceof VarDecl varDecl) {
            visitVarDecl(varDecl);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        TokenType type = constDecl.getBType().getToken().getType();
        for (ConstDef constDef : constDecl.getConstDefs()) {
            visitConstDef(constDef, type);
        }
    }

    private void visitConstDef(ConstDef constDef, TokenType type) {
        String name = constDef.getIdent().getContent();
        if (tableManager.inCurrentTable(name)) {
            ErrorHandler.getInstance().addError(new Error(
                    ErrorType.IdentRedefined, constDef.getIdent().getLine()
            ));
            return;
        }
        VarType varType = switch (type) {
            case INTTK -> VarType.INT;
            case CHARTK -> VarType.CHAR;
            default -> throw new RuntimeException("Shouldn't reach here");
        };
        int dimension = 0;
        int length = 0;
        if (constDef.getConstExp() != null) {
            dimension = 1;
            length = constDef.getConstExp().calculate();
        }
        InitialValue initialValue = null;
        ValueType valueType = type == TokenType.INTTK
                ? IntegerType.i32 : IntegerType.i8;
        if (dimension == 1) {
            valueType = new ArrayType(length, valueType);
        }
        ArrayList<Integer> values = visitConstInitVal(constDef.getConstInitVal(), type);
        initialValue = new InitialValue(valueType, length, values);
        System.out.println(initialValue);
        ConstSymbol constSymbol = new ConstSymbol(name, varType, dimension,
                length, initialValue);
        tableManager.addSymbol(constSymbol);
    }

    private ArrayList<Integer> visitConstInitVal(ConstInitVal constInitVal, TokenType type) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (constInitVal.getConstExp() != null) {
            ans.add(constInitVal.getConstExp().calculate());
        } else if (constInitVal.getConstExps() != null) {
            for (ConstExp constExp : constInitVal.getConstExps()) {
                ans.add(constExp.calculate());
            }
        } else if (constInitVal.getStringConst() != null) {
            assert type == TokenType.CHARTK;
            String str = constInitVal.getStringConst().getContent();
            for (int i = 1; i < str.length() - 1; i++) {
                if (str.charAt(i) == '\\') {
                    i++;
                    int value = switch (str.charAt(i)) {
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
                                + str.charAt(i) + "'");
                    };
                    ans.add(value);
                    continue;
                }
                ans.add((int) str.charAt(i));
            }
            ans.add(0);
        } else {
            throw new RuntimeException("Shouldn't reach here");
        }
        return ans;
    }

    private void visitVarDecl(VarDecl varDecl) {

    }
}
