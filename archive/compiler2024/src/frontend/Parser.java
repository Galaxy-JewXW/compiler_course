package frontend;

import error.ErrorLog;
import error.ErrorType;
import error.ErrorVisitor;
import frontend.Token;
import frontend.TokenType;
import syntax.BType;
import syntax.Block;
import syntax.BlockItem;
import syntax.CompUnit;
import syntax.expression.Cond;
import syntax.ConstDecl;
import syntax.ConstDef;
import syntax.ConstInitVal;
import syntax.Decl;
import syntax.statement.ForStmt;
import syntax.FuncDef;
import syntax.FuncFParam;
import syntax.FuncFParams;
import syntax.FuncRParams;
import syntax.FuncType;
import syntax.InitVal;
import syntax.LVal;
import syntax.MainFuncDef;
import syntax.Number;
import syntax.VarDecl;
import syntax.VarDef;
import syntax.expression.AddExp;
import syntax.expression.ConstExp;
import syntax.expression.EqExp;
import syntax.expression.Exp;
import syntax.expression.LAndExp;
import syntax.expression.LOrExp;
import syntax.expression.MulExp;
import syntax.expression.PrimaryExp;
import syntax.expression.RelExp;
import syntax.expression.UnaryExp;
import syntax.expression.UnaryOp;
import syntax.statement.BlockStmt;
import syntax.statement.BreakStmt;
import syntax.statement.ContinueStmt;
import syntax.statement.ExpStmt;
import syntax.statement.ForStruct;
import syntax.statement.GetintStmt;
import syntax.statement.IfStmt;
import syntax.statement.LValExpStmt;
import syntax.statement.PrintfStmt;
import syntax.statement.ReturnStmt;
import syntax.statement.Stmt;

import java.util.ArrayList;

public class Parser {
    private final ArrayList<Token> tokens;
    private int pos = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public Token curToken() {
        return tokens.get(pos);
    }

    public Token curToken(int offset) {
        return tokens.get(pos + offset);
    }

    public boolean match(TokenType type) {
        if (curTokenType() == type) {
            pos++;
            return true;
        } else {
            return false;
        }
    }

    private void panic(TokenType type) {
        if (!match(type)) {
            doErrorException(type);
        }
    }

    private void doErrorException(TokenType type) {
        int line = curToken(-1).getLine();
        ErrorVisitor errorVisitor = ErrorVisitor.getInstance();
        switch (type) {
            case SEMICN -> errorVisitor.addError(new ErrorLog(ErrorType.SEMICNMissing, line));
            case RPARENT -> errorVisitor.addError(new ErrorLog(ErrorType.RPARENTMissing, line));
            case RBRACK -> errorVisitor.addError(new ErrorLog(ErrorType.RBRACKMissing, line));
            default -> throw new RuntimeException();
        }
    }

    public TokenType curTokenType() {
        return curToken().getType();
    }

    public TokenType curTokenType(int offset) {
        return curToken(offset).getType();
    }

    public CompUnit parse() {
        return parseCompUnit();
    }

    private CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef;
        while (curTokenType(2) != TokenType.LPARENT) {
            decls.add(parseDecl());
        }
        while (curTokenType(1) != TokenType.MAINTK) {
            funcDefs.add(parseFuncDef());
        }
        mainFuncDef = parseMainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }

    private Decl parseDecl() {
        if (match(TokenType.CONSTTK)) {
            return parseConstDecl();
        } else {
            return parseVarDecl();
        }
    }

    private ConstDecl parseConstDecl() {
        BType bType = parseBType();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        constDefs.add(parseConstDef());
        while (match(TokenType.COMMA)) {
            constDefs.add(parseConstDef());
        }
        panic(TokenType.SEMICN);
        return new ConstDecl(bType, constDefs);
    }

    private BType parseBType() {
        panic(TokenType.INTTK);
        return new BType(curToken(-1));
    }

    private ConstDef parseConstDef() {
        panic(TokenType.IDENFR);
        Token ident = curToken(-1);
        ArrayList<ConstExp> constExps = new ArrayList<>();
        while (match(TokenType.LBRACK)) {
            constExps.add(parseConstExp());
            panic(TokenType.RBRACK);
        }
        panic(TokenType.ASSIGN);
        ConstInitVal constInitVal = parseConstInitVal();
        return new ConstDef(ident, constExps, constInitVal);
    }

    private ConstInitVal parseConstInitVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new ConstInitVal(constInitVals);
            }
            constInitVals.add(parseConstInitVal());
            while (!match(TokenType.RBRACE)) {
                panic(TokenType.COMMA);
                constInitVals.add(parseConstInitVal());
            }
            return new ConstInitVal(constInitVals);
        } else {
            return new ConstInitVal(parseConstExp());
        }
    }

    private VarDecl parseVarDecl() {
        BType bType = parseBType();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        varDefs.add(parseVarDef());
        while (match(TokenType.COMMA)) {
            varDefs.add(parseVarDef());
        }
        panic(TokenType.SEMICN);
        return new VarDecl(bType, varDefs);
    }

    private VarDef parseVarDef() {
        panic(TokenType.IDENFR);
        Token ident = curToken(-1);
        ArrayList<ConstExp> constExps = new ArrayList<>();
        while (match(TokenType.LBRACK)) {
            constExps.add(parseConstExp());
            panic(TokenType.RBRACK);
        }
        InitVal initVal = null;
        if (match(TokenType.ASSIGN)) {
            initVal = parseInitVal();
        }
        return new VarDef(ident, constExps, initVal);
    }

    private InitVal parseInitVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<InitVal> initVals = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new InitVal(initVals);
            }
            initVals.add(parseInitVal());
            while (match(TokenType.COMMA)) {
                initVals.add(parseInitVal());
            }
            panic(TokenType.RBRACE);
            return new InitVal(initVals);
        } else {
            return new InitVal(parseExp());
        }
    }

    private FuncDef parseFuncDef() {
        FuncType funcType = parseFuncType();
        Token ident = curToken();
        panic(TokenType.IDENFR);
        panic(TokenType.LPARENT);
        FuncFParams funcFParams = null;
        if (curTokenType() == TokenType.INTTK) {
            funcFParams = parseFuncFParams();
        }
        panic(TokenType.RPARENT);
        return new FuncDef(funcType, ident, funcFParams, parseBlock());
    }

    private MainFuncDef parseMainFuncDef() {
        panic(TokenType.INTTK);
        panic(TokenType.MAINTK);
        panic(TokenType.LPARENT);
        panic(TokenType.RPARENT);
        return new MainFuncDef(parseBlock());
    }

    private FuncType parseFuncType() {
        Token type = curToken();
        if (!match(TokenType.VOIDTK) && !match(TokenType.INTTK)) {
            throw new RuntimeException();
        }
        return new FuncType(type);
    }

    private FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        funcFParams.add(parseFuncFParam());
        while (match(TokenType.COMMA)) {
            funcFParams.add(parseFuncFParam());
        }
        return new FuncFParams(funcFParams);
    }

    private FuncFParam parseFuncFParam() {
        BType bType = parseBType();
        Token ident = curToken();
        boolean isArray = false;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        panic(TokenType.IDENFR);
        if (match(TokenType.LBRACK)) {
            isArray = true;
            panic(TokenType.RBRACK);
            while (match(TokenType.LBRACK)) {
                constExps.add(parseConstExp());
                panic(TokenType.RBRACK);
            }
        }
        return new FuncFParam(bType, ident, constExps, isArray);
    }

    private Block parseBlock() {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        panic(TokenType.LBRACE);
        while (!match(TokenType.RBRACE)) {
            blockItems.add(parseBlockItem());
        }
        int end = curToken(-1).getLine();
        return new Block(blockItems, end);
    }

    private BlockItem parseBlockItem() {
        if (curTokenType() == TokenType.CONSTTK || curTokenType() == TokenType.INTTK) {
            return new BlockItem(parseDecl());
        } else {
            return new BlockItem(parseStmt());
        }
    }

    private Stmt parseStmt() {
        if (match(TokenType.IFTK)) {
            return parseIfStmt();
        } else if (match(TokenType.FORTK)) {
            return parseForStruct();
        } else if (match(TokenType.BREAKTK)) {
            Token token = curToken(-1);
            panic(TokenType.SEMICN);
            return new BreakStmt(token);
        } else if (match(TokenType.CONTINUETK)) {
            Token token = curToken(-1);
            panic(TokenType.SEMICN);
            return new ContinueStmt(token);
        } else if (match(TokenType.RETURNTK)) {
            return parseReturnStmt();
        } else if (match(TokenType.PRINTFTK)) {
            return parsePrintfStmt();
        }
        if (curTokenType() == TokenType.LBRACE) {
            return new BlockStmt(parseBlock());
        }
        if (curTokenType() == TokenType.IDENFR) {
            int temp = pos;
            LVal lVal = parseLVal();
            if (match(TokenType.ASSIGN)) {
                if (match(TokenType.GETINTTK)) {
                    panic(TokenType.LPARENT);
                    panic(TokenType.RPARENT);
                    panic(TokenType.SEMICN);
                    return new GetintStmt(lVal);
                }
                Exp exp = parseExp();
                panic(TokenType.SEMICN);
                return new LValExpStmt(lVal, exp);
            }
            pos = temp;
        }
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = parseExp();
            panic(TokenType.SEMICN);
        }
        return new ExpStmt(exp);
    }

    private IfStmt parseIfStmt() {
        panic(TokenType.LPARENT);
        Cond cond = parseCond();
        panic(TokenType.RPARENT);
        Stmt stmt1 = parseStmt();
        Stmt stmt2 = null;
        if (match(TokenType.ELSETK)) {
            stmt2 = parseStmt();
        }
        return new IfStmt(cond, stmt1, stmt2);
    }

    private ForStmt parseForStmt() {
        LVal lVal = parseLVal();
        panic(TokenType.ASSIGN);
        return new ForStmt(lVal, parseExp());
    }

    private ForStruct parseForStruct() {
        ForStmt forStmt1 = null;
        Cond cond = null;
        ForStmt forStmt2 = null;
        Stmt stmt;
        panic(TokenType.LPARENT);
        if (!match(TokenType.SEMICN)) {
            forStmt1 = parseForStmt();
            panic(TokenType.SEMICN);
        }
        if (!match(TokenType.SEMICN)) {
            cond = parseCond();
            panic(TokenType.SEMICN);
        }
        if (!match(TokenType.RPARENT)) {
            forStmt2 = parseForStmt();
            panic(TokenType.RPARENT);
        }
        stmt = parseStmt();
        return new ForStruct(forStmt1, cond, forStmt2, stmt);
    }

    private ReturnStmt parseReturnStmt() {
        Token token = curToken(-1);
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = parseExp();
            panic(TokenType.SEMICN);
        }
        return new ReturnStmt(token, exp);
    }

    private PrintfStmt parsePrintfStmt() {
        Token token = curToken(-1);
        panic(TokenType.LPARENT);
        Token format = curToken();
        ArrayList<Exp> exps = new ArrayList<>();
        panic(TokenType.STRCON);
        while (!match(TokenType.RPARENT)) {
            panic(TokenType.COMMA);
            exps.add(parseExp());
        }
        panic(TokenType.SEMICN);
        return new PrintfStmt(token, format, exps);
    }

    private Exp parseExp() {
        return new Exp(parseAddExp());
    }

    private Cond parseCond() {
        return new Cond(parseLOrExp());
    }

    private LVal parseLVal() {
        Token token = curToken();
        ArrayList<Exp> exps = new ArrayList<>();
        panic(TokenType.IDENFR);
        while (match(TokenType.LBRACK)) {
            exps.add(parseExp());
            panic(TokenType.RBRACK);
        }
        return new LVal(token, exps);
    }

    private PrimaryExp parsePrimaryExp() {
        if (match(TokenType.LPARENT)) {
            Exp exp = parseExp();
            panic(TokenType.RPARENT);
            return new PrimaryExp(exp);
        }
        if (match(TokenType.INTCON)) {
            return new PrimaryExp(parseNumber());
        }
        return new PrimaryExp(parseLVal());
    }

    private Number parseNumber() {
        return new Number(curToken(-1));
    }

    private boolean isExp() {
        switch (curTokenType()) {
            case PLUS, MINU, NOT, IDENFR, LPARENT, INTCON -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private UnaryExp parseUnaryExp() {
        if (curTokenType() == TokenType.IDENFR &&
                curTokenType(1) == TokenType.LPARENT) {
            Token ident = curToken();
            panic(TokenType.IDENFR);
            panic(TokenType.LPARENT);
            FuncRParams funcRParams = null;
            if (isExp()) {
                funcRParams = parseFuncRParams();
            }
            panic(TokenType.RPARENT);
            return new UnaryExp(ident, funcRParams);
        }
        if (match(TokenType.PLUS) || match(TokenType.MINU) || match(TokenType.NOT)) {
            UnaryOp unaryOp = parseUnaryOp();
            UnaryExp unaryExp = parseUnaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        }
        return new UnaryExp(parsePrimaryExp());
    }

    private UnaryOp parseUnaryOp() {
        return new UnaryOp(curToken(-1));
    }

    private FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(parseExp());
        while (match(TokenType.COMMA)) {
            exps.add(parseExp());
        }
        return new FuncRParams(exps);
    }

    private MulExp parseMulExp() {
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        unaryExps.add(parseUnaryExp());
        while (match(TokenType.MULT) || match(TokenType.DIV) || match(TokenType.MOD)) {
            ops.add(curToken(-1));
            unaryExps.add(parseUnaryExp());
        }
        return new MulExp(unaryExps, ops);
    }

    private AddExp parseAddExp() {
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        mulExps.add(parseMulExp());
        while (match(TokenType.PLUS) || match(TokenType.MINU)) {
            ops.add(curToken(-1));
            mulExps.add(parseMulExp());
        }
        return new AddExp(mulExps, ops);
    }

    private RelExp parseRelExp() {
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        addExps.add(parseAddExp());
        while (match(TokenType.LEQ) || match(TokenType.LSS) || match(TokenType.GRE) || match(TokenType.GEQ)) {
            ops.add(curToken(-1));
            addExps.add(parseAddExp());
        }
        return new RelExp(addExps, ops);
    }

    private EqExp parseEqExp() {
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        relExps.add(parseRelExp());
        while (match(TokenType.EQL) || match(TokenType.NEQ)) {
            ops.add(curToken(-1));
            relExps.add(parseRelExp());
        }
        return new EqExp(relExps, ops);
    }

    private LAndExp parseLAndExp() {
        ArrayList<EqExp> eqExps = new ArrayList<>();
        eqExps.add(parseEqExp());
        while (match(TokenType.AND)) {
            eqExps.add(parseEqExp());
        }
        return new LAndExp(eqExps);
    }

    private LOrExp parseLOrExp() {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        lAndExps.add(parseLAndExp());
        while (match(TokenType.OR)) {
            lAndExps.add(parseLAndExp());
        }
        return new LOrExp(lAndExps);
    }

    private ConstExp parseConstExp() {
        return new ConstExp(parseAddExp());
    }
}
