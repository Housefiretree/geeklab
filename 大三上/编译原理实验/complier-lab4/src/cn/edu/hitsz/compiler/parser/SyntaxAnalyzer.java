package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.*;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//TODO: 实验二: 实现 LR 语法分析驱动程序

/**
 * LR 语法分析驱动程序
 * <br>
 * 该程序接受词法单元串与 LR 分析表 (action 和 goto 表), 按表对词法单元流进行分析, 执行对应动作, 并在执行动作时通知各注册的观察者.
 * <br>
 * 你应当按照被挖空的方法的文档实现对应方法, 你可以随意为该类添加你需要的私有成员对象, 但不应该再为此类添加公有接口, 也不应该改动未被挖空的方法,
 * 除非你已经同助教充分沟通, 并能证明你的修改的合理性, 且令助教确定可能被改动的评测方法. 随意修改该类的其它部分有可能导致自动评测出错而被扣分.
 */
public class SyntaxAnalyzer {
    private final SymbolTable symbolTable;
    private final List<ActionObserver> observers = new ArrayList<>();


    public SyntaxAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * 注册新的观察者
     *
     * @param observer 观察者
     */
    public void registerObserver(ActionObserver observer) {
        observers.add(observer);
        observer.setSymbolTable(symbolTable);
    }

    /**
     * 在执行 shift 动作时通知各个观察者
     *
     * @param currentStatus 当前状态
     * @param currentToken  当前词法单元
     */
    public void callWhenInShift(Status currentStatus, Token currentToken) {
        for (final var listener : observers) {
            listener.whenShift(currentStatus, currentToken);
        }
    }

    /**
     * 在执行 reduce 动作时通知各个观察者
     *
     * @param currentStatus 当前状态
     * @param production    待规约的产生式
     */
    public void callWhenInReduce(Status currentStatus, Production production) {
        for (final var listener : observers) {
            listener.whenReduce(currentStatus, production);
        }
    }

    /**
     * 在执行 accept 动作时通知各个观察者
     *
     * @param currentStatus 当前状态
     */
    public void callWhenInAccept(Status currentStatus) {
        for (final var listener : observers) {
            listener.whenAccept(currentStatus);
        }
    }


    List<Token> tokenList = new ArrayList<>();
    public void loadTokens(Iterable<Token> tokens) {
        // TODO: 加载词法单元
        // 你可以自行选择要如何存储词法单元, 譬如使用迭代器, 或是栈, 或是干脆使用一个 list 全存起来
        // 需要注意的是, 在实现驱动程序的过程中, 你会需要面对只读取一个 token 而不能消耗它的情况,
        // 在自行设计的时候请加以考虑此种情况
        //throw new NotImplementedException();

        for (Token token : tokens) {
            //System.out.println("token:"+token);
            tokenList.add(token);
        }
        //System.out.println("tokenList:"+tokenList);
    }


    Status init_status;
    LRTable lrTable;
    public void loadLRTable(LRTable table) {
        // TODO: 加载 LR 分析表
        // 你可以自行选择要如何使用该表格:
        // 是直接对 LRTable 调用 getAction/getGoto, 抑或是直接将 initStatus 存起来使用
        //throw new NotImplementedException();

        //将初始状态保存下来
        init_status = table.getInit();
        //System.out.println("init status:"+init_status);

        lrTable = table;

    }

    public void run() {
        // TODO: 实现驱动程序
        // 你需要根据上面的输入来实现 LR 语法分析的驱动程序
        // 请分别在遇到 Shift, Reduce, Accept 的时候调用上面的 callWhenInShift, callWhenInReduce, callWhenInAccept
        // 否则用于为实验二打分的产生式输出可能不会正常工作
        //throw new NotImplementedException();


        //建立状态栈，符号栈并且进行初始化
        //新建状态栈，把初始状态压栈
        Stack<Status> statusStack = new Stack<>();
        statusStack.push(init_status);
        //System.out.println("当前状态栈顶："+statusStack.peek());
        //新建符号栈，把结束符号$压栈
        //注意处理符号栈需要另外写一个symbol类
        Stack<Symbol> symbolStack = new Stack<>();
        Symbol end_symbol = new Symbol(tokenList.get(tokenList.size()-1));
        symbolStack.push(end_symbol);
        //System.out.println("当前符号栈顶："+symbolStack.peek());

        //当tokenList不为空时，获取当前的token,status和action，然后根据当前action，利用switch-case进行代码编写
        //每个action的情况要考虑栈、tokenList和callWhen函数(注意有哪些做法)。规约时要产生式。。。
        int i=0;
        int flag=0;
        while(i<tokenList.size()){
            if(flag==1){break;}
            //System.out.println("当前i:"+i);

            //获取待读入的下一个token
            Token token_now = tokenList.get(i);
            //System.out.println("待读入token:"+token_now);

            //获取状态栈栈顶元素
            Status status_now = statusStack.peek();
            //System.out.println("状态栈栈顶元素："+status_now);

            //根据状态栈栈顶元素和待读入的下一个token查询判断下一个待执行动作
            Action action = lrTable.getAction(status_now,token_now);
            //System.out.println("因此当前采取行动："+action);

            switch (action.getKind()) {
                case Shift -> {
                    //如果是shift，把action对应的状态压入状态栈，对应的token压入符号栈
                    final var shiftTo = action.getStatus();
                    statusStack.push(shiftTo);
                    Symbol shiftSymbol = new Symbol(token_now);
                    symbolStack.push(shiftSymbol);
                    //由于是移入动作，下次应当读下一个token了
                    i++;
                    //调用相关call
                    callWhenInShift(status_now,token_now);
                }
                case Reduce -> {
                    //如果是reduce，根据产生式长度，符号栈和状态栈均弹出对应长度个token和状态
                    final var production = action.getProduction();
                    int length = production.body().size();
                    for(int j=0;j<length;j++){
                        symbolStack.pop();
                        statusStack.pop();
                    }
                    //产生式左侧的非终结符压入符号栈
                    Symbol reduceSymbol = new Symbol(production.head());
                    symbolStack.push(reduceSymbol);
                    //根据符号栈和状态栈栈顶状态获取goto表的状态，压入状态栈
                    Status reduceStatus = lrTable.getGoto(statusStack.peek(),symbolStack.peek().Symbol_getNonTerminal());
                    statusStack.push(reduceStatus);
                    //调用相关call
                    callWhenInReduce(status_now,production);
                }
                case Accept -> {
                    //如果是accept，语法分析执行结束
                    //调用相关call
                    callWhenInAccept(status_now);
                    flag=1;
                }
                case Error -> {
                    //如果是error
                    //System.out.println("出错!");
                }
                default -> {
                    //do nothing
                }
            }


        }

    }
}
