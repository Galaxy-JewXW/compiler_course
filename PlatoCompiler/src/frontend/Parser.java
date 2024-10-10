package frontend;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.syntax.Character;
import frontend.syntax.Number;
import frontend.syntax.*;
import frontend.syntax.expression.*;
import frontend.syntax.function.*;
import frontend.syntax.statement.*;
import frontend.syntax.variable.*;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

/**
 * 语法分析类，通过接受一系列token，生成一棵语法树。
 * 同时进行简单的异常处理：判断右花括号，右圆括号，分号是否缺失
 */
public class Parser {
    private static final Set<TokenType> EXP_START_TOKENS = EnumSet.of(
            TokenType.PLUS, TokenType.MINU, TokenType.NOT, TokenType.IDENFR,
            TokenType.LPARENT, TokenType.INTCON, TokenType.CHRCON
    );
    private final ArrayList<Token> tokens;
    private int pos = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    // 提供“向前看”和“向后看”
    public Token curToken() {
        return tokens.get(pos);
    }

    public Token curToken(int offset) {
        return tokens.get(pos + offset);
    }

    public TokenType curTokenType() {
        return curToken().getType();
    }

    public TokenType curTokenType(int offset) {
        return curToken(offset).getType();
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
        ErrorHandler errorHandler = ErrorHandler.getInstance();
        switch (type) {
            case SEMICN -> errorHandler.addError(new Error(ErrorType.SEMICNMissing, line));
            case RPARENT -> errorHandler.addError(new Error(ErrorType.RPARENTMissing, line));
            case RBRACK -> errorHandler.addError(new Error(ErrorType.RBRACKMissing, line));
            default -> throw new RuntimeException("What the f: " + type);
        }
    }

    public CompUnit parse() {
        return parseCompUnit();
    }

    private CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        while (curTokenType(2) != TokenType.LPARENT) {
            decls.add(parseDecl());
        }
        while (curTokenType(1) != TokenType.MAINTK) {
            funcDefs.add(parseFuncDef());
        }
        MainFuncDef mainFuncDef = parseMainFuncDef();
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
        do {
            constDefs.add(parseConstDef());
        } while (match(TokenType.COMMA));
        panic(TokenType.SEMICN);
        return new ConstDecl(bType, constDefs);
    }

    private BType parseBType() {
        if (curTokenType() == TokenType.INTTK) {
            match(TokenType.INTTK);
            return new BType(curToken(-1));
        } else if (curTokenType() == TokenType.CHARTK) {
            match(TokenType.CHARTK);
            return new BType(curToken(-1));
        } else {
            throw new RuntimeException();
        }
    }

    private ConstDef parseConstDef() {
        match(TokenType.IDENFR);
        Token ident = curToken(-1);
        ConstExp constExp = null;
        if (match(TokenType.LBRACK)) {
            constExp = parseConstExp();
            panic(TokenType.RBRACK);
        }
        match(TokenType.ASSIGN);
        ConstInitVal constInitVal = parseConstInitVal();
        return new ConstDef(ident, constExp, constInitVal);
    }

    private ConstInitVal parseConstInitVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<ConstExp> constExps = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new ConstInitVal(constExps);
            }
            constExps.add(parseConstExp());
            while (!match(TokenType.RBRACE)) {
                match(TokenType.COMMA);
                constExps.add(parseConstExp());
            }
            return new ConstInitVal(constExps);
        } else if (match(TokenType.STRCON)) {
            return new ConstInitVal(curToken(-1));
        } else {
            return new ConstInitVal(parseConstExp());
        }
    }

    private VarDecl parseVarDecl() {
        BType bType = parseBType();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        do {
            varDefs.add(parseVarDef());
        } while (match(TokenType.COMMA));
        panic(TokenType.SEMICN);
        return new VarDecl(bType, varDefs);
    }

    private VarDef parseVarDef() {
        match(TokenType.IDENFR);
        Token ident = curToken(-1);
        ConstExp constExp = null;
        if (match(TokenType.LBRACK)) {
            constExp = parseConstExp();
            panic(TokenType.RBRACK);
        }
        InitVal initVal = null;
        if (match(TokenType.ASSIGN)) {
            initVal = parseInitVal();
        }
        return new VarDef(ident, constExp, initVal);
    }

    private InitVal parseInitVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<Exp> exps = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new InitVal(exps);
            }
            do {
                exps.add(parseExp());
            } while (match(TokenType.COMMA));
            panic(TokenType.RBRACE);
            return new InitVal(exps);
        } else if (match(TokenType.STRCON)) {
            return new InitVal(curToken(-1));
        } else {
            return new InitVal(parseExp());
        }
    }

    private FuncDef parseFuncDef() {
        FuncType funcType = parseFuncType();
        match(TokenType.IDENFR);
        Token ident = curToken(-1);
        match(TokenType.LPARENT);
        FuncFParams funcFParams = null;
        if (curTokenType() == TokenType.INTTK
                || curTokenType() == TokenType.CHARTK) {
            funcFParams = parseFuncFParams();
        }
        panic(TokenType.RPARENT);
        Block block = parseBlock();
        return new FuncDef(funcType, ident, funcFParams, block);
    }

    private MainFuncDef parseMainFuncDef() {
        match(TokenType.INTTK);
        match(TokenType.MAINTK);
        panic(TokenType.LPARENT);
        panic(TokenType.RPARENT);
        Block block = parseBlock();
        return new MainFuncDef(block);
    }

    private FuncType parseFuncType() {
        Token funcType = curToken();
        if (!match(TokenType.VOIDTK) && !match(TokenType.INTTK) && !match(TokenType.CHARTK)) {
            throw new RuntimeException();
        }
        return new FuncType(funcType);
    }

    private FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        do {
            funcFParams.add(parseFuncFParam());
        } while (match(TokenType.COMMA));
        return new FuncFParams(funcFParams);
    }

    private FuncFParam parseFuncFParam() {
        BType bType = parseBType();
        match(TokenType.IDENFR);
        Token ident = curToken(-1);
        boolean isArray = false;
        if (match(TokenType.LBRACK)) {
            isArray = true;
            panic(TokenType.RBRACK);
        }
        return new FuncFParam(bType, ident, isArray);
    }

    private Block parseBlock() {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        match(TokenType.LBRACE);
        while (!match(TokenType.RBRACE)) {
            blockItems.add(parseBlockItem());
        }
        // end是语句块的右花括号所在的行
        int end = curToken(-1).getLine();
        return new Block(blockItems, end);
    }

    private BlockItem parseBlockItem() {
        if (curTokenType() == TokenType.CONSTTK
                || curTokenType() == TokenType.INTTK
                || curTokenType() == TokenType.CHARTK) {
            return new BlockItem(parseDecl());
        } else {
            return new BlockItem(parseStmt());
        }
    }

    private Stmt parseStmt() {
        if (match(TokenType.BREAKTK)) {
            Token token = curToken(-1);
            panic(TokenType.SEMICN);
            return new BreakStmt(token);
        } else if (match(TokenType.CONTINUETK)) {
            Token token = curToken(-1);
            panic(TokenType.SEMICN);
            return new ContinueStmt(token);
        } else if (match(TokenType.RETURNTK)) {
            return parseReturnStmt();
        } else if (match(TokenType.IFTK)) {
            return parseIfStmt();
        } else if (match(TokenType.FORTK)) {
            return parseForStruct();
        } else if (match(TokenType.PRINTFTK)) {
            return parsePrintfStmt();
        }
        if (curTokenType() == TokenType.LBRACE) {
            return new BlockStmt(parseBlock());
        }
        if (curTokenType() == TokenType.IDENFR) {
            int tempPos = pos;
            LVal lVal = parseLVal();
            if (match(TokenType.ASSIGN)) {
                if (match(TokenType.GETINTTK)) {
                    panic(TokenType.LPARENT);
                    panic(TokenType.RPARENT);
                    panic(TokenType.SEMICN);
                    return new GetintStmt(lVal);
                } else if (match(TokenType.GETCHARTK)) {
                    panic(TokenType.LPARENT);
                    panic(TokenType.RPARENT);
                    panic(TokenType.SEMICN);
                    return new GetcharStmt(lVal);
                }
                Exp exp = parseExp();
                panic(TokenType.SEMICN);
                return new LValExpStmt(lVal, exp);
            }
            pos = tempPos;
        }
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = parseExp();
            panic(TokenType.SEMICN);
        }
        return new ExpStmt(exp);
    }

    private ReturnStmt parseReturnStmt() {
        Exp exp = null;
        Token token = curToken(-1);
        if (!match(TokenType.SEMICN)) {
            if (isExp()) {
                exp = parseExp();
            }
            panic(TokenType.SEMICN);
        }
        return new ReturnStmt(token, exp);
    }

    private IfStmt parseIfStmt() {
        match(TokenType.LPARENT);
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
        match(TokenType.ASSIGN);
        Exp exp = parseExp();
        return new ForStmt(lVal, exp);
    }

    private ForStruct parseForStruct() {
        match(TokenType.LPARENT);
        ForStmt forStmt1 = null;
        if (!match(TokenType.SEMICN)) {
            forStmt1 = parseForStmt();
            panic(TokenType.SEMICN);
        }
        Cond cond = null;
        if (!match(TokenType.SEMICN)) {
            cond = parseCond();
            panic(TokenType.SEMICN);
        }
        ForStmt forStmt2 = null;
        if (!match(TokenType.RPARENT)) {
            forStmt2 = parseForStmt();
            panic(TokenType.RPARENT);
        }
        Stmt stmt = parseStmt();
        return new ForStruct(forStmt1, cond, forStmt2, stmt);
    }

    private PrintfStmt parsePrintfStmt() {
        Token token = curToken(-1);
        match(TokenType.LPARENT);
        Token stringConst = curToken();
        match(TokenType.STRCON);
        ArrayList<Exp> exps = new ArrayList<>();
        while (match(TokenType.COMMA)) {
            exps.add(parseExp());
        }
        panic(TokenType.RPARENT);
        panic(TokenType.SEMICN);
        return new PrintfStmt(token, stringConst, exps);
    }

    private Exp parseExp() {
        return new Exp(parseAddExp());
    }

    private Cond parseCond() {
        return new Cond(parseLOrExp());
    }

    private LVal parseLVal() {
        Token ident = curToken();
        match(TokenType.IDENFR);
        Exp exp;
        if (match(TokenType.LBRACK)) {
            exp = parseExp();
            panic(TokenType.RBRACK);
            return new LVal(ident, exp);
        } else {
            return new LVal(ident);
        }
    }

    private PrimaryExp parsePrimaryExp() {
        if (match(TokenType.LPARENT)) {
            Exp exp = parseExp();
            panic(TokenType.RPARENT);
            return new PrimaryExp(exp);
        } else if (match(TokenType.INTCON)) {
            return new PrimaryExp(parseNumber());
        } else if (match(TokenType.CHRCON)) {
            return new PrimaryExp(parseCharacter());
        } else {
            return new PrimaryExp(parseLVal());
        }
    }

    private Number parseNumber() {
        return new Number(curToken(-1));
    }

    private Character parseCharacter() {
        return new Character(curToken(-1));
    }

    private boolean isExp() {
        return EXP_START_TOKENS.contains(curTokenType());
    }

    private UnaryExp parseUnaryExp() {
        if (curTokenType() == TokenType.IDENFR && curTokenType(1) == TokenType.LPARENT) {
            Token ident = curToken();
            match(TokenType.IDENFR);
            match(TokenType.LPARENT);
            FuncRParams funcRParams = null;
            // 这里判断是否存在实参，exp的first集合即为EXP_FIRST。
            if (isExp()) {
                funcRParams = parseFuncRParams();
            }
            panic(TokenType.RPARENT);
            return new UnaryExp(ident, funcRParams);
        } else if (match(TokenType.PLUS)
                || match(TokenType.MINU)
                || match(TokenType.NOT)) {
            UnaryOp unaryOp = parseUnaryOp();
            UnaryExp unaryExp = parseUnaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        } else {
            PrimaryExp primaryExp = parsePrimaryExp();
            return new UnaryExp(primaryExp);
        }
    }

    private UnaryOp parseUnaryOp() {
        return new UnaryOp(curToken(-1));
    }

    private FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        do {
            exps.add(parseExp());
        } while (match(TokenType.COMMA));
        return new FuncRParams(exps);
    }

    private MulExp parseMulExp() {
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Token> operators = new ArrayList<>();
        unaryExps.add(parseUnaryExp());
        while (match(TokenType.MULT) || match(TokenType.DIV) || match(TokenType.MOD)) {
            operators.add(curToken(-1));
            unaryExps.add(parseUnaryExp());
        }
        return new MulExp(unaryExps, operators);
    }

    private AddExp parseAddExp() {
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Token> operators = new ArrayList<>();
        mulExps.add(parseMulExp());
        while (match(TokenType.PLUS) || match(TokenType.MINU)) {
            operators.add(curToken(-1));
            mulExps.add(parseMulExp());
        }
        return new AddExp(mulExps, operators);
    }

    private RelExp parseRelExp() {
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Token> operators = new ArrayList<>();
        addExps.add(parseAddExp());
        while (match(TokenType.LEQ) || match(TokenType.GEQ)
                || match(TokenType.LSS) || match(TokenType.GRE)) {
            operators.add(curToken(-1));
            addExps.add(parseAddExp());
        }
        return new RelExp(addExps, operators);
    }

    private EqExp parseEqExp() {
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Token> operators = new ArrayList<>();
        relExps.add(parseRelExp());
        while (match(TokenType.EQL) || match(TokenType.NEQ)) {
            operators.add(curToken(-1));
            relExps.add(parseRelExp());
        }
        return new EqExp(relExps, operators);
    }

    private LAndExp parseLAndExp() {
        ArrayList<EqExp> eqExps = new ArrayList<>();
        do {
            eqExps.add(parseEqExp());
        } while (match(TokenType.AND));
        return new LAndExp(eqExps);
    }

    private LOrExp parseLOrExp() {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        do {
            lAndExps.add(parseLAndExp());
        } while (match(TokenType.OR));
        return new LOrExp(lAndExps);
    }

    private ConstExp parseConstExp() {
        return new ConstExp(parseAddExp());
    }

}
