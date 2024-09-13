# 2023-BUAA-Compiler

## 一、参考编译器(Pascal-S-Compiler)介绍

### 总体结构：

包括词法分析，语法分析，语义分析、代码生成，符号表管理和错误处理

### 接口设计：

#### 1.实用工具：

errormsg：输出错误信息

endskip：源程序出错后在输入被跳过的部分打印下划线

error( n: integer )：n：错误信息种类。功能：打印出错位置和出错编号

fatal( n: integer )：n:表格编号。功能：打印表格溢出信息

options：处理编译时的可选项

switch( var b: boolean )：b:是否打印相关表格的特征变量。功能:处理编译可选项中的'+','-'标志

setup：建立初始信息

enterids：在符号表中登录标准的类型（基本类型），函数和过程的名字，以及它们的相应信息

#### 2.词法分析：

nextch：读取下一个字符，处理行结束符

insymbol：读取下一单词符号，处理注释行

readscale：处理实数的指数部分

adjustscale：根据小数位数和指数大小求出实数数值

#### 3.语法分析：

1.block( fsys: symset; isfun: boolean; level: integer )：分析处理分程序。参数 fsys是传入的 test集合检验符号合法性，容错处理，参数 idfun是分程序的类型（是否是函数），参数level是处理的分程序所在的层数；

parameterlist：处理形式参数表

skip( fsys:symset; n:integer)：跳读源程序，直至取来的符号属于给出的符号集为止,并打印出出错标志，参数 fsys是给定的符号集，参数n是错误编号；

test( s1,s2: symset; n:integer )：testsemicolon：测试当前符号是否为分号；

2.typ( fsys: symset; var tp: types; var rf,sz:integer )：处理类型描述，由参数得到它的类型tp,指向类型详细信息表的指针 ref和该类型的大小，参数fsys是合法的符号集合，用来检测字符的合法性，参数tp是返回参数的类型，参数rf是返回参数的详细信息表的指针，参数 sz是返回该类型的大小；

arraytyp( var aref, arsz: integer )：处理数组类型,由参数返回值指向该数组信息向量表的指针aref和数组大小 arsz，参数 aref是返回该数组信息向量表的指针，参数 arsz是返回该参数的大小；

constant( fsys: symset; var c: conrec )：处理程序中出现的常量,并由参数c返回该常量的类型和数值，参数fsys是给定的检测符号集合，参数c是返回该常量的类型和数值；

3.expression(fsys:symset; var x:item)：分析处理表达式，由参数x返回求值结果的类型

selector(fsys:symset; var v:item)：处理结构变量，数组下标变量或记录成员变量，参数 fsys是合法字符集合，检测字符是否合法，参数v是一个结构体，结构体中的元素typ是类型，用于判断v是一个数组还是一个记录，元素index是 v在 btab或者atab中的索引；

simpleexpression( fsys: symset; var x: item )：处理简单表达式,由参数x返回求值结果的类型，参数 fsys是合法字符集合，参数x是返回值；

term( fsys: symset; var x: item )：处理项，由参数返回结果类型

factor( fsys: symset; var x: item )：处理因子,由参数返回结果类型

standfct( n: integer )：处理标准函数调用，参数 n是标准函数编码

4.call( fsys: symset; i:integer )：处理非标准的过程或函数调用，参数fsys是合法的字符集合，对字符的合法性进行检测，参数 i是被调用过程或者函数在tab表中的位置；

5.statement( fsys:symset )：分析处理各种语句

ifstatement：处理if

casestatement：处理case

repeatstatement：处理repeat

whilestatement：处理while

forstatement：处理for

assignment( lv, ad: integer )：处理赋值语句

compoundstatement：处理复合语句

caselabel：处理case语句中的标号，将各标号对应的目标代码入口地址填入 case表中，并检查标号有无重复定义

onecase：处理case语句的一个分支

standproc( n: integer )：处理标准输入/输出过程调用

#### 4.解释执行：

emit( fct: integer )：生成P-code，没有操作数，参数 fct是助记符编号

emit1( fct, b: integer )：生成P-code，只有一个操作数，参数 fct是助记符编号，参数 b是第二个操作数；

emit2( fct, a, b: integer )：生成P-code，有两个操作数，参数 fct是助记符编号，参数 a是第一个操作数，参数b是第二个操作数；

interpret：p-code解释执行程序

dump：程序运行时，卸出打印现场剖析信息 display,t,b以及运行栈s的内容，满足编译预选项的要求

#### 5.符号表管理：

enter(x0:alfa; x1:objecttyp; x2:types; x3:integer )：x0:名字;x1:种类; x2:类型;x3:地址; 功能：把标准类型、过程和函数的名字登录到符号表中

enterarray( tp: types; l,h: integer )：登录数组信息向量表，参数 tp是数组的类型，参数l是数组的下界，参数h是数组的上界

enterblock：登录分程序表

enterreal( x: real )：登录实常数表

enter( id: alfa; k:objecttyp )：参数 id是名字，k是种类；功能：在符号表中登录分程序说明部分出现的名字

entervariable：将变量名登录到符号表中

constdec：处理常量定义，将常量名及其相应信息填入符号表

typedeclaration：处理类型定义，并将类型名及其相关信息填入符号表

variabledeclaration：处理变量定义，并将变量名及相应信息填入符号表

procdeclaration：处理过程或者函数说明，将过程名填入符号表，递归调用block分析处理程序(层次 level+1)；

printtables：打印编译生成的符号表,分程序表,实常量数表,以及P-code表；

### 文件组织：

全部功能在一个文件内完成，较为耦合。

## 二、编译器总体设计

### **总体结构**：

总体结构分为前端，中端，后端三个部分。

* 前端：负责词法分析，语法分析
* 中端：语义分析与中间代码生成，中端优化
* 后端：目标代码生成，后端优化

此外错误处理安排在前端完成后进行。

错误处理，中间代码生成，目标代码生成分别维护一套符号表。

### **接口设计**：

前端：

* 词法分析（Lexer） 输入：读取源程序得到的符号串； 输出：处理得到的token列表
* 语法分析（Parser）输入：Lexer输出的token列表；输出：语法树

错误处理：输入：Parser得到的语法树；输出：是否具有错误以及排序后的错误信息

中端：

* 中间代码生成：输入：Parser得到的语法树；输出：中间代码IRModule
* 中间代码优化：输入：中间代码IRModule；输出：优化后的中间代码IRModule

后端：输入：中间代码IRModule；输出：目标代码MIPS

### **文件组织**：

```
├─backend                   #后端：目标代码生成与后端优化   
│  └─Symbol                 #后端维护的符号表  
├─config                    #配置：包括是否优化，设置输出路径等  
├─error                     #错误处理
│  └─symbol                 #错误处理维护的符号表   
├─frontend                  #前端：包括词法分析和语法分析
├─IO                        #输入与输出
├─IR                        #中端：中间代码生成   
│  ├─SymbolTable            #中端维护的符号表
│  ├─types
│  └─values
│      └─instructions
│          ├─men
│          └─terminator
├─node                      #前端语法分析部分语法树节点
│  ├─decl
│  ├─expression
│  ├─func
│  └─stmt
├─pass                      #遍：代码优化
│  └─Analysis   
└─token                     #前端词法分析部分生成的token
```

## 三、词法分析设计


设计思路是根据词法规则构建状态图实现词法分析程序

```java
if (isIdHead(in.charAt(pos))) {
    while (isIdHead(in.charAt(pos)) || Character.isDigit(in.charAt(pos))) {
        catToken();
    }
    reserve();
} else if (Character.isDigit(in.charAt(pos))) {
    symbol = TokenType.INTCON;
    while (Character.isDigit(in.charAt(pos))) {
        catToken();
    }
} else if (in.charAt(pos) == '"') {
    symbol = TokenType.STRCON;
    catToken();
    while (in.charAt(pos) != '"') {
        catToken();
    }
    catToken();
} else if (in.charAt(pos) == '!') {
......
```

比如说如果遇到IDHead(下划线和字母)那么就进入到识别Ident和保留字的状态；遇到数字进入到识别数字的状态；遇到引号就进入到识别字符串的状态……直到不再匹配。

最初设计中我没有考虑到其它模块中token的使用形式，而且也不熟悉枚举类的toString()方法，因此只是以字符串的方式保留了每个token的symbol。导致语法分析获取token信息时十分不便。所以便新建Token类和TokenType类，用以记录Token信息，便于后续使用。

```java
public class Token {
    private final TokenType symbol;
    private final String token;
    private final int line;
    ......
}
```

在完成词法分析编码后，我发现在本地运行时出错，最终发现是'\\r'导致的，因此将'\r'一并视作空白字符。

## 四、语法分析设计

设计思路是通过递归下降程序来构建语法树。

### 左递归和回溯问题

**解决左递归问题：**

将AddExp → MulExp | AddExp ('+' | '−') MulExp 改为 AddExp → MulExp {  ('+' | '−') MulExp }  。MulExp等等同理。

**解决回溯问题：**

Stmt → LVal '=' Exp ';' |  [Exp] ';' | LVal '=' 'getint''('')'';' 对于这部分语法，可以发现Exp和LVal的FIRST集合里是有IDENFR的，同时仅仅偷看几个字符是不够的（LVal可以是数组），为此不可避免的需要先解析LVal然后才能确定当前语法成分究竟是什么。因此我的解决方案是，记录当前token指针，然后解析LVal,进而确定是哪种语法成分，如果最终确定是Stmt → [Exp] ';' 就需要将token指针还原到解析LVal之前的部分。这也是将所有token记录到一个列表的优点：方便回退。

### 递归下降

通过向前偷看字符来确定当前语法成分，从而调用相应的递归下降程序返回解析好的相应的语法成分。

```java
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
```

### 信息组织形式

目标结果是得到语法树。为此我为每一种语法成分都建立了单独的语法树节点。特别的，stmt语法成分较多，将stmt单独设为接口，由PrintStmt,BreakStmt等实现接口，减少耦合。最终，通过递归下降得到语法树根节点CompUnit。

```java
public class CompUnit {
    //CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    public void print() {
        for (Decl decl : decls) {
            decl.print();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.print();
        }
        mainFuncDef.print();
        OutputHandler.println("<CompUnit>");
    }
}
```

### 编码后的修改

编码后的修改主要是为了适配错误处理。语法分析部分主要处理的错误主要是缺少分号、右小括号和右中括号，当发生这些错误时需要记录下来。同时也对我原本的递归下降逻辑进行了一些修改。举个列子，下面是varDecl解析程序的改变。

语法：VarDecl → BType VarDef { ',' VarDef } ';'

修改前：当出现分号缺失的错误时，会陷入死循环

```java
while (!match(TokenType.SEMICN)) {
    if (!match(TokenType.COMMA)) {
        error();
    }
    varDefs.add(varDef());
}
```

修改后：修正了死循环问题

```java
while (match(TokenType.COMMA)) {
    varDefs.add(varDef());
}
mustMatch(TokenType.SEMICN);  
```

## 五、错误处理设计

设计思路是，遍历语法树，维护符号表，检查每种语法成分的错误。

### 符号表管理

符号设计：顶层Symbol记录姓名和类型，底层FuncSymbol,VarSymbol继承Symbol。其中VarSymbol额外记录自身维数和是否是常量，FuncSymbol额外记录函数参数，参数记录自身名称、维数和类型。

符号表设计：采用栈式符号表，每层符号表用HashMap建立名称和Symbol的映射，同时每层表还记录了本层的类型，如果不在函数内部，类型为null，这样设计为了检查当前函数类型。

压入一层符号表：进入CompUnit（顶层符号表）， FuncDef，MainFuncDef，BlockStmt时

弹出一层符号表：离开FuncDef，MainFuncDef，BlockStmt时

### 单例模式

采用单例模式创建ErrorHandler，内部管理栈式符号表，提供管理符号表接口，同时能够解析函数参数，记录循环层数（break,continue检查），记录错误类型，输出错误信息。

### 错误检查

遍历语法树进行检查。

错误检查中最复杂的部分是函数参数的检查部分，这部分设计的主要是对UnaryExp的检查。

```java
if (funcRParams != null) {
    funcRParams.checkError();
}
if (!ErrorHandler.getInstance().defined(Ident.getToken())) {
    ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.c, Ident.getLine()));
    return;
}
//没有定义int a; a()这样的错误;暂时忽略;
FuncSymbol symbol = (FuncSymbol) ErrorHandler.getInstance().getSymbol(Ident.getToken());
//参数个数检查
if (checkParamNum(symbol)) {
    return;
}
//参数类型匹配检查
checkParamType(symbol);
```

参数个数检查：注意检查当函数参数为空的情况

参数类型匹配检查：对于函数调用的检查，其实只需检查参数Exp的第一个子表达式即可比如说a[0]+1,检查a[0]是否符合参数类型即可。解析参数后，如果该参数是void 类型则匹配失败，对于变量类型作为参数，维数为定义时的维数-传参的维数，比如说 int a[10][10][10]; f(a[1][2]); 参数维数就是3-2=1维。其余的情况（数字）维数为0。当维数与函数定义的参数维数不匹配时匹配失败。

此外还有循环检查，当经过for语句时需要先增加一层循环。检查Break和Continue时需要判断是否在循环内部。

## 六、代码生成设计

代码生成中我选择LLVM中间代码，然后将LLVM翻译为MIPS。

### 中间代码生成LLVM

#### 整体结构

```java
public class IRModule {
    private static final IRModule irModule = new IRModule();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    public static IRModule getInstance() {
        return irModule;
    }
    ......
}
```

中间代码生成的目的是生成便于代码优化的IRModule，IRModule采用单例设计。

IRModule记录着全局变量和函数，函数又由若干个基本块组成。

#### Value-User

所有的类都（多层）继承了Value

```java
public class Value {
    private String name;
    private Type type;
    private final ArrayList<Use> useList = new ArrayList<>();
    public static int valNumber = 0;
    ......
}
public class User extends Value {
    private final ArrayList<Value> operands;
    ......
}
public class Use {
    private Value value;
    private User user;
    private int pos;
    ......
}
```

User使用Value，在添加使用的Value到Operands时，也会创建Use添加到Value的useList中，这样可以从User找到使用的所有Value,也可以从Value找到所有使用它的User，为后续代码优化中的def-use分析提供了便利。

#### 类型系统

顶层Type作为接口，底层ArrayType，FuncType，IntegerType，LabelType，PointerType，VoidType实现了Type接口。其中LabelType作为BasicBlock的类型。其余类型的使用符合常识。

#### Function

```java
public class Function extends Value {
    private final boolean isLibrary;
    private final ArrayList<Argument> arguments;
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    ......
}
```

函数会记录自身是否是库函数，以及定义的参数和包含的基本块。

#### BasicBlock

```java
public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private boolean isTerminated = false;
    ......
}

```

BasicBlock作为存储指令的基本单位，最后一条指令必定由终结指令（BrInst或RetInst）组成。

当基本块添加了终结指令后便不会再添加指令。

#### Instruction

每种指令都分别建类管理。每种指令对应若干种操作，但是一种操作对应一种翻译方式。

```java
public enum Operator {
    //基本运算
    Add, Sub, Mul, SDiv, Mod,
    //逻辑运算,signed
    Eq, Ne, Sgt, Sge, Slt, Sle,
    //类型转换
    Zext,
    //函数调用
    Call,
    //内存操作
    Alloc, Store, Load, GEP, Phi,
    //跳转指令
    br, ret,
    PC, MOVE,
}
```

Instruction创建时会被添加到BasicBlock里, BasicBlock创建时会被添加到Function里

#### BuildFactory

采用单例模式，负责创建Value的作用，能够创建函数，基本块，指令，常量等。

#### 符号表管理

中间代码生成也维护一套栈式符号表。

```java
public class SymbolTable {
    private final ArrayList<HashMap<String, Value>> symbolTables;
    private final ArrayList<HashMap<String, Integer>> constTables;
    ......
}
```

其中constTables是为了处理 `const int a=10;int b[a];`这样的操作。记住常量的值便于直接获取常量的值，减少对Value的限制。

#### visitor

生成中间代码程序，依然是递归下降程序，解析语法树节点进而得到LLVM语法节点。

#### 短路求值

下面我举了if(cond) stmt 的例子

```java
BasicBlock trueBlock = buildFactory.unnamedBasicBlock();
BasicBlock falseBlock = buildFactory.unnamedBasicBlock();
curTrueBlock = trueBlock;
curFalseBlock = falseBlock;
visitCond(ifStmt.getCond());
trueBlock.refill(curFunction);
curBlock = trueBlock;
visitStmt(ifStmt.getStmt1());
buildFactory.brInst(curBlock, falseBlock);
falseBlock.refill(curFunction);
curBlock = falseBlock;

```

在我的设计中变量命名是LLVM从0开始命名的方式，这对命名有很大的限制（虽然后面做优化的时候，我还会有重命名操作），所以我采用了重填的方式。curTrueBlock和curFalseBlock分别指向条件为真/假的基本块。在解析Cond的时候会生成跳转语句，能够判断为真时（如1||a）直接跳到curTrueBlock,否则跳到下一基本块继续进行判断，能够判断为假时(如0&&a)直接跳到curFalseBlock。当解析完了Cond之后进行重填基本块，为其正确命名和添加到函数基本块列表里。

#### 数组

类型对应ArrayType, 对应GlobelVar或是AllocaInst,类型为指向ArrayType的PointerType。

GEP指令主要在解析左值LVal时生成。

### 目标代码生成MIPS

#### 符号表管理

```java
public class MipsSymbol {
    private String base;//$gp,$fp
    private final int off;
    private final Value val;
    ......
}
```

主要目的是维护栈空间的分配，$gp在后续编码中没有实现。

同样是实现了栈式符号表。

#### 栈空间分配

（最初设计时，我还没有分配寄存器，把所有变量都分配在栈上。）

记录栈指针偏移量spOff，给int类型分配空间每次spOff减4, 数组翻译上，数组名分配4字节空间，记录着数组首地址，然后给数组元素分配连续地址。要获取数组元素地址只需要数组首地址加偏移量即可，这样设计是为了和全局数组保持一致。

函数参数也在栈上有对应的位置，即使分配了寄存器也会在栈上保留空间。

调用函数时需要保存现场和调整栈指针，然后跳到这个函数，最后恢复现场。

#### 翻译策略

针对每一条中间指令直接翻译到相应的MIPS代码。设计上的遗憾：应当先输出到Buffer里然后进行窥孔优化，删掉不必要的Move指令等等。在我的架构里，为了保证可扩展性和减少代码长度，我都会先将值加载到寄存器里然后进行运算，这就产生了一些不必要的move等等指令。

#### 寄存器分配

见代码优化部分

## 七、代码优化设计

### 中端优化

#### char2str

实现输出字符到输出字符串，首先得了解LLVM里字符串输出的格式

```

@hs_new_global_str_0 = dso_local constant [10 x i8] c"21373499\0a\00"

%1 = getelementptr inbounds [10 x i8], [10 x i8]* @hs_new_global_str_0, i32 0, i32 0

call void @putstr(i8* %1)

```

\n 需要被替换为 \0a ,并且字符串尾部需要加上\00 , gepInst中识别到目标是字符串时需要加上inbounds

实现中比较困扰的是字符串的命名问题，严格来说应当保存所有的全局变量的名字，避免命名重复的问题，但我在这部分没有过于较真，采用了前缀+字符串id这样的设计

#### Mem2Reg

这部分优化的工作主要分为插入Phi和变量重命名，以将生成的中间代码转化为真正的SSA格式

##### 计算CFG图：

这部分很明显就是要根据BrInst来构建，某个基本块跳转到另一个基本块便需要添加相应的next和prev关系

在我的设计里，如果一个基本块添加了终结指令（Ret和Br）,那么后续便不会添加指令，但是指令依旧会被创建，所以我在指令内部修改基本块前驱后继关系的时候便出现了bug。BrInst必须得检测基本块是否终结来判断是否添加前驱后继关系。

##### 计算支配关系：

我采用的方法是根据基本块的前驱后继关系，从第一个基本块开始DFS，当遍历到目标节点时便返回。这样所有必须经过目标基本块的基本块，即被目标基本块支配的基本块就被确认了出来。

在我的设计里出现 `for(;;){}`的情况时，会生成无法到达的基本块，从而导致这样的基本块会被所有基本块支配，于是会出现a严格支配b，b严格支配a这样的情况，所以在进行Mem2Reg之前需要把所有的无法到达的基本块先删掉。

##### 计算直接支配关系：

遍历bb所严格支配的基本块a,然后将a与bb所严格支配的其它基本块比较，如果a不严格支配其它基本块，那么便可以确定bb支配a。

##### 计算支配边界：


这部分按照课程组给出的伪代码便可求出，我遇到的主要问题就是没删无法到达的基本块导致直接支配节点出错

##### 插入Phi:


伪代码可以看出是对每一个变量名都要进行插入Phi的操作(这里的变量并不包括数组类型)。

load的指令便是use，store指令是def,同时新添加的Phi既是def又是use。

通过上述伪代码即可在支配边界（不同基本块的汇合处）插入Phi。

##### 变量重命名：

1.从第一个基本块开始DFS

2.针对某一变量v：如果基本块内指令是相关的Alloca指令，直接删掉，如果是store指令更新v.reachingDef ,如果是load指令将该load指令替换为v.reachingDef, 同时Phi也是def，需要更新v.reachingDef。然而，如果当前没有v.reachingDef,那么v.reachingDef对应的就是0。

3.更新Phi的值：Phi指令对Phi指令所在基本块的每个前驱基本块都有一个对应的值。因此需要更新当前基本块的后继基本块对应Phi指令的值为v.reachingDef

4.对所有该基本块直接支配的基本块进行变量重命名。

#### 死代码删除

在这部分我实现的策略是：

首先判断某个指令是否是有用的指令（具体体现为LLVM中没有分配变量名的指令和Call指令）如果是有用的指令就找到它的闭包，然后不在有用指令闭包里的指令便可以删掉。

死代码删除我进行了两次，分别在GVN的前后。

#### GVN

我消除局部公共子表达式是在函数范围内进行的，每个函数一张表，前序遍历支配树以保证遍历到某一指令时，其使用的变量都在前面定义过。

进入某一基本块时，首先进行常量折叠，这一部分我优化的是二元加减乘除取模运算，可以分为三种情况：

* 对于运算数有两个常量的情况：直接进行运算，并将原来的指令替换为该结果
* 对于运算数有一个常量的情况：优化了 `a+0,a-0,a*0,a*1,0/a,a/1,0%a,a%1`这几种情况
* 对于运算数没有常量的情况：优化了 `a+(b-a),(a+b)+(a-b),a-a,(a+b)-a,a-(a-b),a/a,a%a`这几种情况

然后检测表中是否有该指令对应的哈希值，如果有则进行替换，没有就加入该哈希值

然后DFS该基本块直接支配的基本块

从某个基本块退出时，需要注意删除其记录的哈希值，避免影响其兄弟节点

在进行第一遍GVN之后，我还进行了一次运算优化，因为第一次的GVN之后暴露出了更多的优化机会。

进行完GVN后我还进行了一次死代码删除。

### 后端优化

进入到后端部分，我首先删除了多余的指令Zext,因为它在MIPS翻译过程中没有任何意义

#### 寄存器分配

我首先进行的寄存器分配，然后才进行的消除Phi。因为消除Phi过程中会影响我的def-use关系，所以我将分配寄存器提前到消除Phi之前了。

首先介绍的寄存器分工：

`$0,$1,$ra,$fp,$gp,$sp这几个寄存器有原本的分工所以不做分配 `

`$a0,$v0库函数传参经常修改，所以我不做分配`

`$t0-$t9 和 $s0-$s7拿来做全局寄存器分配`

`$k0,$k1拿来做临时寄存器，$v1拿来做具有一定持久能力的临时寄存器，因为翻译GEP等指令时需要对某些值进行记录`

`$a1-$a3分配给函数的参数`

寄存器分配以函数为单位，在分配寄存器前首先进行变量活性分析:

首先计算出def-use,并且从后向前遍历基本块计算in-out,当不再发生变化时便停止计算

$ out[m] \gets\cup_{s \in succ[m]} in[s] $

$ in[m] \gets use[m] \cup (out[m]-def[m]) $

冲突分析：in集合里的所有变量必然是冲突的，毕竟它们不可能都存在一个寄存器里，然后每一个指令和此时活跃的变量冲突，此时活跃的变量就是后续还可能用到的变量，可以理解为out集合的变量和该指令后面的指令用到的变量，因此构建冲突图时可以倒序遍历指令序列，存储用到的指令，这样遍历到某个指令时就知道那些指令是活跃的了。

在寄存器分配上，我建立在支配树和冲突分析的基础，遍历到某一个指令时，首先查看当前指令使用的值是否还活跃，如果不活跃了，可以将该值使用的寄存器释放，然后给指令分配寄存器，如果寄存器不够分配了，就随机释放一个。然后前序遍历支配树，来给后续的基本块分配寄存器，如果后续基本块的in没有某个变量，可以将分配给该变量的寄存器释放，等处理完该后继基本块后再恢复，处理完全部直接支配的节点，便可以释放给当前基本块def的变量分配的寄存器。

寄存器相关的bug主要是翻译MIPS过程中出现的：

首先是函数调用问题：

因为寄存器分配是以函数为分配单位的，这就导致不同的函数可能使用了相同的寄存器，所以在调用函数时需要先压栈，调用完了在取出来。最开始我的设计是，a调用b函数时，只压入a和b冲突的寄存器，b调用c时只压入b和c冲突的寄存器，这就导致没有压入a和c冲突的变量。比较合理的做法是应当先记录每一个函数修改的寄存器，其中包括调用其它函数修改的寄存器，调用函数时，压入两个函数修改寄存器的交集。我采取了比较简单的方法，就是压入了当前函数修改的全部寄存器。

此外对于函数传参时寄存器的问题，出现了f(a,b,c)调用f(b,c,a) 时赋值 `$a1 <- $a2, $a2 <- $a3, $a3 <- $a1` 这样调用的实际效果是调用f(b,c,b), 对于这样的情况，我的解决方法是直接从栈上取相应的值（因为调用函数时我会压入所有使用的寄存器）

在临时寄存器分配上：

我最初采用的是所有的临时寄存器进行轮转分配,但这样导致我在翻译GEP指令时，需要某一个临时寄存器来记住运算结果，但是这个寄存器会被轮转重复分配，我采用的就是把$v1,作为专门担任这样功能的临时寄存器使用，不参与轮转分配。

此外还经常会出现，某个指令使用的值和该指令分配到同一个寄存器，如果该指令翻译的过程中直接对该指令的寄存器进行修改就会影响到结果，解决方案是先对临时寄存器进行修改最后赋值到目标寄存器里。这样可能会带来一定的损失，比较好的解决方案是进行窥孔优化，来判断是否需要临时寄存器。

#### 消除Phi

翻译MIPS前需要把Phi都消除，转化为move指令。采用的方式是把Phi首先转化为同步赋值语句PC然后再转化为move指令。

PC指令插入效果：每个基本块中最多只有一个PC指令，完成对后继Phi的赋值。对于某个有Phi的基本块a，需要在它的前序基本块b完成对Phi的赋值，如果b的后继只有a，那么可以直接完成赋值，但是如果后继不止a一个，就需要在a和b之间插入一个基本块mid,来进行对Phi的赋值。

move指令插入效果：把PC指令替换为move,同时PC指令是同步赋值语句，这样如果对于move序列其中a在b的前面，如果a的dst是b的src，翻译效果就会有影响，需要加入中间变量tmp 赋值为b的src，b赋值时dst赋值为tmp, 同时如果a的dst和b的src分配了相同的寄存器也需要进行相同的操作。

#### 指令选择

在指令选择上不能选用add，addi这样的指令，而应当选用addu,addiu避免溢出检查导致的ObjectError。

在条件跳转指令上，我选择了blez而不是bgtz，这样的好处是能够节省j指令。

#### 乘除法优化

乘法优化：

乘法代价为5，这样就使得优化空间比较小。乘法的优化是变量乘常数的情况。我优化的情况有：

* -1*a直接negu指令操作即可
* 常数在二进制情况下只有一个1的情况：判断方法是abs&(abs-1)==0, abs代表常数的绝对值，这样情况下直接移位和根据常数正负判断是否取负即可相应的变式有 `a*(imm-1) => a*imm -a 和 a*(imm+1)=>转化为a*imm+a 其中imm 表示只有一位1的情况`
* 常数在二进制情况下有两个1的情况：比如说10这种，转化为两个移位相加即可。

除法优化：

除法代价为25，优化空间很大，能够优化的情况是变量除常数的情况。

* 如果除-1，取相反数即可
* 如果除只有一位1的数，举个例子：-3>>1如果直接移位结果是-2，正常除2的结果应当是-1，当负数不能整除的情况下，需要加一个偏置常数才能保证结果正确。
* 除以其它常数的情况下，我依照的是论文**Division by Invariant Integers using Multiplication**：找到multiplier和shift。

取模优化：

代价同样是25，优化思路是转为为a-a/b*b,利用优化过的除法和乘法进行优化。

#### 基本块优化

我采用的策略是：在翻译j指令是如果跳转到的基本块是相邻的下一个基本块，就可以不翻译，这也是采用blez来代替bgtz的原因：修改跳转逻辑来减少j指令的使用。

此外教程还推荐翻译成do-while来减少j指令的使用，但我把for改为do-while之后性能反而降低了，所以没有采用。
