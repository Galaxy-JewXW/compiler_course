package frontend;

import config.Config;
import error.ErrorNode;
import error.ErrorHandler;
import error.ErrorType;
import frontend.Lexer;
import node.*;
import node.Number;
import node.decl.*;
import node.expression.*;
import node.ForStmt;
import node.func.*;
import node.stmt.*;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private final ArrayList<Token> tokens;
    private int index;

    public Parser(Lexer lexer) {
        this.tokens = lexer.getTokens();
        this.index = 0;
    }

    public CompUnit compUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef;
        while (!Objects.equals(tokens.get(index + 2).getSymbol(), TokenType.LPARENT)) {
            Decl decl = decl();
            decls.add(decl);
        }
        while (!Objects.equals(tokens.get(index + 1).getSymbol(), TokenType.MAINTK)) {
            FuncDef funcDef = funcDef();
            funcDefs.add(funcDef);
        }
        mainFuncDef = mainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }

    private Decl decl() {
        if (match(TokenType.CONSTTK)) {
            return constDecl();
        } else {
            return varDecl();
        }//else error
    }

    private ConstDecl constDecl() {
        //const has been matched
        BType bType = bType();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        constDefs.add(constDef());
        while (match(TokenType.COMMA)) {
            constDefs.add(constDef());
        }
        mustMatch(TokenType.SEMICN);
        return new ConstDecl(bType, constDefs);
    }

    private BType bType() {
        mustMatch(TokenType.INTTK);
        return new BType(tokens.get(index - 1));
    }

    private ConstDef constDef() {
        mustMatch(TokenType.IDENFR);
        Token Ident = tokens.get(index - 1);
        ArrayList<ConstExp> constExps = new ArrayList<>();
        while (match(TokenType.LBRACK)) {
            ConstExp constExp = constExp();
            constExps.add(constExp);
            mustMatch(TokenType.RBRACK);
        }
        mustMatch(TokenType.ASSIGN);
        ConstInitVal constInitVal = constInitVal();
        return new ConstDef(Ident, constExps, constInitVal);
    }

    private ConstExp constExp() {
        AddExp addExp = addExp();
        return new ConstExp(addExp);
    }

    private ConstInitVal constInitVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new ConstInitVal(constInitVals);
            }
            constInitVals.add(constInitVal());
            while (!match(TokenType.RBRACE)) {
                mustMatch(TokenType.COMMA);
                ConstInitVal constInitVal = constInitVal();
                constInitVals.add(constInitVal);
            }
            return new ConstInitVal(constInitVals);
        } else {
            ConstExp constExp = constExp();
            return new ConstInitVal(constExp);
        }
    }

    private VarDecl varDecl() {
        BType bType = bType();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        varDefs.add(varDef());
        while (match(TokenType.COMMA)) {
            varDefs.add(varDef());
        }
        mustMatch(TokenType.SEMICN);
        return new VarDecl(bType, varDefs);
    }

    private VarDef varDef() {
        Token Ident = tokens.get(index);
        ArrayList<ConstExp> constExps = new ArrayList<>();
        InitVal InitVal = null;
        mustMatch(TokenType.IDENFR);
        while (match(TokenType.LBRACK)) {
            constExps.add(constExp());
            mustMatch(TokenType.RBRACK);
        }
        if (match(TokenType.ASSIGN)) {
            InitVal = initVal();
        }
        return new VarDef(Ident, constExps, InitVal);
    }

    private Exp exp() {
        return new Exp(addExp());
    }

    private InitVal initVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<InitVal> initVals = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new InitVal(initVals);
            }
            initVals.add(initVal());
            while (match(TokenType.COMMA)) {
                initVals.add(initVal());
            }
            mustMatch(TokenType.RBRACE);
            return new InitVal(initVals);
        } else {
            return new InitVal(exp());
        }
    }

    private FuncDef funcDef() {
        FuncType funcType = funcType();
        Token Ident = tokens.get(index);
        FuncFParams funcFParams = null;
        mustMatch(TokenType.IDENFR);
        mustMatch(TokenType.LPARENT);
        if (tokens.get(index).getSymbol() == TokenType.INTTK) {
            funcFParams = funcFParams();
        }
        mustMatch(TokenType.RPARENT);
        Block block = block();
        return new FuncDef(funcType, Ident, funcFParams, block);
    }

    private FuncType funcType() {
        Token funcType = tokens.get(index);
        if (!match(TokenType.VOIDTK) && !match(TokenType.INTTK)) {
            error(TokenType.INTTK);//error(TokenType.VOIDTK)
        }
        return new FuncType(funcType);
    }

    private FuncFParams funcFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        {
            FuncFParam funcFParam = funcFParam();
            funcFParams.add(funcFParam);
        }
        while (match(TokenType.COMMA)) {
            FuncFParam funcFParam = funcFParam();
            funcFParams.add(funcFParam);
        }
        return new FuncFParams(funcFParams);
    }

    private FuncFParam funcFParam() {
        BType bType = bType();
        Token Ident = tokens.get(index);
        boolean isArray = false;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        mustMatch(TokenType.IDENFR);
        if (match(TokenType.LBRACK)) {
            isArray = true;
            mustMatch(TokenType.RBRACK);
            while (match(TokenType.LBRACK)) {
                ConstExp constExp = constExp();
                constExps.add(constExp);
                mustMatch(TokenType.RBRACK);
            }
        }
        return new FuncFParam(bType, Ident, constExps, isArray);
    }

    private Block block() {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        mustMatch(TokenType.LBRACE);
        while (!match(TokenType.RBRACE)) {
            BlockItem blockItem = blockItem();
            blockItems.add(blockItem);
        }
        int endLine = tokens.get(index - 1).getLine();
        return new Block(blockItems, endLine);
    }

    private BlockItem blockItem() {
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.CONSTTK)
                || Objects.equals(tokens.get(index).getSymbol(), TokenType.INTTK)) {
            Decl decl = decl();
            return new BlockItem(decl);
        } else {
            Stmt stmt = stmt();
            return new BlockItem(stmt);
        }
    }

    private Stmt stmt() {
        if (match(TokenType.IFTK)) {
            return ifStmt();
        }
        if (match(TokenType.FORTK)) {
            return stmtFor();
        }
        if (match(TokenType.BREAKTK)) {
            Token breakToken = tokens.get(index - 1);
            mustMatch(TokenType.SEMICN);
            return new BreakStmt(breakToken);
        }
        if (match(TokenType.CONTINUETK)) {
            Token continueToken = tokens.get(index - 1);
            mustMatch(TokenType.SEMICN);
            return new ContinueStmt(continueToken);
        }
        if (match(TokenType.RETURNTK)) {
            return returnStmt();
        }
        if (match(TokenType.PRINTFTK)) {
            return printfStmt();
        }
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.LBRACE)) {
            return new BlockStmt(block());
        }
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.IDENFR)) {
            int tmpIndex = index;
            LVal lVal = lVal();
            if (match(TokenType.ASSIGN)) {
                if (match(TokenType.GETINTTK)) {
                    mustMatch(TokenType.LPARENT);
                    mustMatch(TokenType.RPARENT);
                    mustMatch(TokenType.SEMICN);
                    return new AssignGetintStmt(lVal);
                }
                Exp exp = exp();
                mustMatch(TokenType.SEMICN);
                return new AssignExpStmt(lVal, exp);
            }
            index = tmpIndex;
        }
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = exp();
            mustMatch(TokenType.SEMICN);
        }
        return new ExpStmt(exp);
    }

    private Cond cond() {
        return new Cond(lOrExp());
    }

    private IfStmt ifStmt() {
        //这里if已经被匹配过了
        Cond cond;
        Stmt stmt1;
        Stmt stmt2 = null;
        mustMatch(TokenType.LPARENT);
        cond = cond();
        mustMatch(TokenType.RPARENT);
        stmt1 = stmt();
        if (match(TokenType.ELSETK)) {
            stmt2 = stmt();
        }
        return new IfStmt(cond, stmt1, stmt2);
    }

    private For stmtFor() {
        //for已经被匹配过了
        ForStmt forStmt1 = null;
        Cond cond = null;
        ForStmt forStmt2 = null;
        Stmt stmt;
        mustMatch(TokenType.LPARENT);
        if (!match(TokenType.SEMICN)) {
            forStmt1 = forStmt();
            mustMatch(TokenType.SEMICN);
        }
        if (!match(TokenType.SEMICN)) {
            cond = cond();
            mustMatch(TokenType.SEMICN);
        }
        if (!match(TokenType.RPARENT)) {
            forStmt2 = forStmt();
            mustMatch(TokenType.RPARENT);
        }
        stmt = stmt();
        return new For(forStmt1, cond, forStmt2, stmt);
    }

    private ForStmt forStmt() {
        LVal lVal = lVal();
        mustMatch(TokenType.ASSIGN);
        return new ForStmt(lVal, exp());
    }

    private ReturnStmt returnStmt() {
        //return 已经被匹配过了
        Token returnToken = tokens.get(index - 1);
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = exp();
            mustMatch(TokenType.SEMICN);
        }
        return new ReturnStmt(returnToken, exp);
    }

    private PrintfStmt printfStmt() {
        //print已经被匹配过了
        Token printfToken = tokens.get(index - 1);
        mustMatch(TokenType.LPARENT);
        Token token = tokens.get(index);
        ArrayList<Exp> exps = new ArrayList<>();
        mustMatch(TokenType.STRCON);
        while (!match(TokenType.RPARENT)) {
            mustMatch(TokenType.COMMA);
            exps.add(exp());
        }
        mustMatch(TokenType.SEMICN);
        return new PrintfStmt(printfToken, token, exps);
    }

    private LVal lVal() {
        Token token = tokens.get(index);
        ArrayList<Exp> exps = new ArrayList<>();
        mustMatch(TokenType.IDENFR);
        while (match(TokenType.LBRACK)) {
            exps.add(exp());
            mustMatch(TokenType.RBRACK);
        }
        return new LVal(token, exps);
    }

    private AddExp addExp() {
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        MulExp mulExp = mulExp();
        mulExps.add(mulExp);
        while (match(TokenType.PLUS) || match(TokenType.MINU)) {
            Token op = tokens.get(index - 1);
            ops.add(op);
            mulExps.add(mulExp());
        }
        return new AddExp(mulExps, ops);
    }

    private MulExp mulExp() {
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        {
            UnaryExp unaryExp = unaryExp();
            unaryExps.add(unaryExp);
        }
        while (match(TokenType.MULT) || match(TokenType.DIV) || match(TokenType.MOD)) {
            Token op = tokens.get(index - 1);
            ops.add(op);
            UnaryExp unaryExp = unaryExp();
            unaryExps.add(unaryExp);
        }
        return new MulExp(unaryExps, ops);
    }

    private LOrExp lOrExp() {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        lAndExps.add(lAndExp());
        while (match(TokenType.OR)) {
            lAndExps.add(lAndExp());
        }
        return new LOrExp(lAndExps);
    }

    private LAndExp lAndExp() {
        ArrayList<EqExp> eqExps = new ArrayList<>();
        eqExps.add(eqExp());
        while (match(TokenType.AND)) {
            eqExps.add(eqExp());
        }
        return new LAndExp(eqExps);
    }

    private EqExp eqExp() {
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        relExps.add(relExp());
        while (match(TokenType.EQL) || match(TokenType.NEQ)) {
            ops.add(tokens.get(index - 1));
            relExps.add(relExp());
        }
        return new EqExp(relExps, ops);
    }

    private RelExp relExp() {
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        addExps.add(addExp());
        while (match(TokenType.LSS) || match(TokenType.LEQ)
                || match(TokenType.GRE) || match(TokenType.GEQ)) {
            ops.add(tokens.get(index - 1));
            addExps.add(addExp());
        }
        return new RelExp(addExps, ops);
    }

    private boolean isExp() {
        switch (tokens.get(index).getSymbol()) {
            case PLUS, MINU, NOT, IDENFR, LPARENT, INTCON -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private UnaryExp unaryExp() {
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.IDENFR) &&
                Objects.equals(tokens.get(index + 1).getSymbol(), TokenType.LPARENT)) {
            Token ident = tokens.get(index);
            FuncRParams funcRParams = null;
            index += 2;
            if (isExp()) {
                funcRParams = funcRParams();
            }
            mustMatch(TokenType.RPARENT);
            return new UnaryExp(ident, funcRParams);
        }
        Token op = tokens.get(index);
        if (match(TokenType.PLUS) || match(TokenType.MINU) || match(TokenType.NOT)) {
            UnaryOp unaryOp = new UnaryOp(op);
            UnaryExp unaryExp = unaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        }
        return new UnaryExp(primaryExp());
    }

    private PrimaryExp primaryExp() {
        if (match(TokenType.LPARENT)) {
            Exp exp = exp();
            mustMatch(TokenType.RPARENT);
            return new PrimaryExp(exp);
        }
        if (match(TokenType.INTCON)) {
            return new PrimaryExp(new Number(tokens.get(index - 1)));
        }
        LVal lVal = lVal();
        return new PrimaryExp(lVal);
    }

    private FuncRParams funcRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(exp());
        while (match(TokenType.COMMA)) {
            exps.add(exp());
        }
        return new FuncRParams(exps);
    }

    private MainFuncDef mainFuncDef() {
        mustMatch(TokenType.INTTK);
        mustMatch(TokenType.MAINTK);
        mustMatch(TokenType.LPARENT);
        mustMatch(TokenType.RPARENT);
        Block block = block();
        return new MainFuncDef(block);
    }

    private boolean match(TokenType tokenType) {
        if (tokens.get(index).getSymbol() == tokenType) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    private void error(TokenType tokenType) {
        if (!Config.checkError) {
            throw new RuntimeException("error at index:" + index
                    + "\nthis token is:" + tokens.get(index));//unexpected error
        }
        int preLine = tokens.get(index - 1).getLine();
        ErrorHandler errorHandler = ErrorHandler.getInstance();
        switch (tokenType) {
            case SEMICN -> errorHandler.addError(new ErrorNode(ErrorType.i, preLine));
            case RPARENT -> errorHandler.addError(new ErrorNode(ErrorType.j, preLine));
            case RBRACK -> errorHandler.addError(new ErrorNode(ErrorType.k, preLine));
            default -> throw new RuntimeException("error at index:" + index
                    + "\nthis token is:" + tokens.get(index));//unexpected error
        }
    }

    private void mustMatch(TokenType tokenType) {
        if (!match(tokenType)) {
            error(tokenType);
        }
    }
}
